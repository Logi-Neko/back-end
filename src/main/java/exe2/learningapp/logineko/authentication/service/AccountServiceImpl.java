package exe2.learningapp.logineko.authentication.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import exe2.learningapp.logineko.authentication.client.IdentityClient;
import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.dtos.account.AccountDTO;
import exe2.learningapp.logineko.authentication.dtos.account.Credentials;
import exe2.learningapp.logineko.authentication.dtos.account.TokenExchangeResponse;
import exe2.learningapp.logineko.authentication.dtos.account.UserCreationParams;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.enums.Role;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.exception.ErrorNormalizer;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService , UserDetailsService {
    private final AccountRepository accountRepository;
    private final KeycloakService keycloakService;
    private final IdentityClient identityClient;
    private final ErrorNormalizer errorNormalizer;
    private final CurrentUserProvider currentUserProvider;

    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${google.oauth.client-id}")
    private String googleClientId;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public AccountDTO.AccountResponse register(AccountDTO.CreateAccountRequest request) {
        try {
            TokenExchangeResponse response = exchangeToken();
            log.info("Obtained access token from Keycloak: {}", response.accessToken());
            // Tạo user trên Keycloak
            var creationUser = identityClient.createUser(
                    "Bearer " + response.accessToken(),
                    UserCreationParams.builder()
                            .username(request.username())
                            .email(request.email())
                            .firstName("System")
                            .lastName(request.fullName())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credentials.builder()
                                    .type("password")
                                    .value(request.password())
                                    .temporary(false)
                                    .build()))
                            .build()
            );

            String userId = extractUserIdFromLocation(creationUser);
            log.info("Created user in Keycloak with ID: {}", userId);

            // Lưu account trong DB
            var account = Account.builder()
                    .email(request.email())
                    .firstName("System")
                    .userId(userId)
                    .lastName(request.fullName())
                    .username(request.username())
//                    .roles(Collections.singleton(Role.USER))
                    .active(true)
                    .build();

            accountRepository.saveAndFlush(account);
            return mapToDTO(account);
        }
        catch (FeignException e) {
            throw errorNormalizer.handleKeycloakError(e);
        }

    }


    private String extractUserIdFromLocation(ResponseEntity<?> response) {
        String location = response.getHeaders().getFirst("Location");
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location header is null or empty");
        }
        String[] segments = location.split("/");
        return segments[segments.length - 1];
    }

    @Override
    public List<AccountDTO.AccountResponse> getAllUsers() {
        List<Account> accounts = accountRepository.findAll();
        if(accounts.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return accounts.stream().map(this::mapToDTO).toList();
    }

    @Override
    public AccountDTO.AccountResponse getUserInfo() {
        Account currentUser = currentUserProvider.getCurrentUser();
        return mapToDTO(currentUser);
    }

    @Override
    public TokenExchangeResponse login(AccountDTO.LoginRequest loginRequest) {
        try {
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("grant_type", "password");
//            requestBody.add("scope", "openid profile email");
            requestBody.add("username", loginRequest.username());
            requestBody.add("password", loginRequest.password());

            return identityClient.exchangeToken(requestBody);
        } catch (FeignException e) {
            log.info("Failed to login with Keycloak: {}", e.getMessage());
            if( e.status() == 401) {
                throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS);
            }
            throw errorNormalizer.handleLoginKeycloakError(e);
        }
    }
    @Override
    public void sendResetPasswordEmail(String username) {
        Keycloak keycloak = null;
        try {
            keycloak = keycloakService.getKeyCloakInstance();

            List<UserRepresentation> users = keycloak.realm(realm) // Sử dụng biến realm
                    .users()
                    .search(username);

            if (users.isEmpty()) {
                throw new AppException(ErrorCode.NOT_FOUND_USERNAME);
            }
            String userId = users.getFirst().getId();
            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .executeActionsEmail(List.of("UPDATE_PASSWORD"));

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error sending reset password email for user: {}", username, e);
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    private TokenExchangeResponse exchangeToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("grant_type", "client_credentials");
        return identityClient.exchangeToken(requestBody);
    }
    @Override
    public TokenExchangeResponse refreshToken(String refreshToken) {
        try {
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("grant_type", "refresh_token");
            requestBody.add("refresh_token", refreshToken);

            return identityClient.exchangeToken(requestBody);
        } catch (FeignException e) {
            log.info("Failed to refresh token: {}", e.getMessage());
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("refresh_token", refreshToken);
            identityClient.logout(body);
        } catch (FeignException e) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    @Override
    public void resetPassword( String oldPassword, String newPassword) {
        Keycloak keycloak = null;
        try  {
            Account currentUser =  currentUserProvider.getCurrentUser();
            String username = currentUser.getUsername();
            if (!validateCurrentPassword(username, oldPassword)) {
                throw new AppException(ErrorCode.AUTH_INVALID_PASSWORD);
            }

            keycloak = keycloakService.getKeyCloakInstance();
            List<UserRepresentation> users = keycloak.realm(realm)
                    .users()
                    .search(username);

            if (users.isEmpty()) {
                throw new AppException(ErrorCode.ERR_NOT_FOUND);
            }

            String userId = users.getFirst().getId();
            CredentialRepresentation newCred = new CredentialRepresentation();
            newCred.setType(CredentialRepresentation.PASSWORD);
            newCred.setValue(newPassword);
            newCred.setTemporary(false);
            keycloak.realm(realm).users().get(userId).resetPassword(newCred);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error resetting password for user: {}", currentUserProvider.getCurrentUser().getUsername(), e);
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    @Override
    public TokenExchangeResponse loginGoogle(String idToken) {
        try {
            log.info("🔄 Starting Google authentication with Keycloak impersonation...");

            // Step 1: Verify Google ID token
            GoogleIdToken.Payload payload = verifyGoogleIdToken(idToken);
            if (payload == null) {
                throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS);
            }

            // Step 2: Extract user info
            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String googleUserId = payload.getSubject();
            String picture = (String) payload.get("picture");

            log.info("✅ Google ID token verified for: {}", email);

            // Step 3: Find or create user
            String userId = findOrCreateKeycloakUser(email, firstName, lastName, googleUserId, picture);

            // Step 4: Generate tokens via impersonation (NO PASSWORD NEEDED!)
            return generateUserTokenDirectly(userId);

        } catch (Exception e) {
            log.error("❌ Google authentication failed: {}", e.getMessage());
            if (e instanceof AppException) {
                throw e;
            }
            throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }

    private AccountDTO.AccountResponse mapToDTO(Account account) {
        return new AccountDTO.AccountResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getLastName()
        );
    }

    private GoogleIdToken.Payload verifyGoogleIdToken(String idToken) {
        try {
            log.info("\uD83D\uDD0D Verifying Google ID token...{}", idToken);
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId)) // Web Client ID
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                return googleIdToken.getPayload();
            }

            log.error("❌ Google ID token verification failed - invalid token");
            return null;

        } catch (Exception e) {
            log.error("❌ Google ID token verification error: {}", e.getMessage());
            return null;
        }
    }

    private String findOrCreateKeycloakUser(String email, String firstName, String lastName,
                                            String googleUserId, String picture) {
        Keycloak keycloak = null;
        try {
            keycloak = keycloakService.getKeyCloakInstance();

            // Search existing user
            List<UserRepresentation> existingUsers = keycloak.realm(realm)
                    .users()
                    .search(email, true);

            if (!existingUsers.isEmpty()) {
                UserRepresentation existingUser = existingUsers.get(0);
                updateUserGoogleAttributes(existingUser, googleUserId, picture, keycloak);
                log.info("👤 Found existing user: {}", email);
                return existingUser.getId();
            }

            // Create new user
            return createNewGoogleUser(email, firstName, lastName, googleUserId, picture, keycloak);

        } catch (Exception e) {
            log.error("❌ Error managing user: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    private String createNewGoogleUser(String email, String firstName, String lastName,
                                       String googleUserId, String picture, Keycloak keycloak) {
        try {
            log.info("🆕 Creating new Google user in Keycloak: {}", email);

            // Generate unique username in case email conflicts
            String username = generateUniqueUsername(email, keycloak);

            // Sanitize names to remove invalid characters
            String sanitizedFirstName = sanitizeName(firstName);
            String sanitizedLastName = sanitizeName(lastName);

            UserRepresentation userRep = new UserRepresentation();
            userRep.setUsername(username);
            userRep.setEmail(email);
            userRep.setFirstName(sanitizedFirstName);
            userRep.setLastName(sanitizedLastName);
            userRep.setEnabled(true);
            userRep.setEmailVerified(true);

            // Google attributes
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("googleUserId", Collections.singletonList(googleUserId));
            attributes.put("provider", Collections.singletonList("google"));
            if (picture != null) {
                attributes.put("picture", Collections.singletonList(picture));
            }
            // Store original names as attributes if they were sanitized
            if (!Objects.equals(firstName, sanitizedFirstName)) {
                attributes.put("originalFirstName", Collections.singletonList(firstName != null ? firstName : ""));
            }
            if (!Objects.equals(lastName, sanitizedLastName)) {
                attributes.put("originalLastName", Collections.singletonList(lastName != null ? lastName : ""));
            }
            userRep.setAttributes(attributes);

            log.info("📝 User data prepared - username: {}, email: {}, firstName: {}, lastName: {}",
                    username, email, sanitizedFirstName, sanitizedLastName);

            // Create user in Keycloak
            Response response = keycloak.realm(realm).users().create(userRep);
            int status = response.getStatus();

            log.info("📡 Keycloak response status: {}", status);

            if (status == 201) {
                String userId = getCreatedUserId(response);
                log.info("✅ Created new Google user: {} with ID: {}", email, userId);

                // Create user in local database with original names
                saveGoogleUserToDatabase(email, firstName, lastName, userId);

                return userId;
            } else if (status == 409) {
                // User already exists, try to find and return existing user
                log.warn("⚠️ User already exists in Keycloak, attempting to find existing user: {}", email);
                return findExistingUserByEmail(email, keycloak);
            } else {
                // Log response body for debugging
                String responseBody = "";
                try {
                    if (response.hasEntity()) {
                        responseBody = response.readEntity(String.class);
                    }
                } catch (Exception e) {
                    log.warn("Could not read response body: {}", e.getMessage());
                }

                log.error("❌ Failed to create user in Keycloak. Status: {}, Body: {}", status, responseBody);
                throw new AppException(ErrorCode.ERR_SERVER_ERROR);
            }

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error creating user: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    private String sanitizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }

        // Remove invalid characters for Keycloak names
        // Keep only letters, numbers, spaces, hyphens, and common accents
        String sanitized = name.trim()
                // Remove parentheses and their content
                .replaceAll("\\([^)]*\\)", "")
                // Remove other special characters except letters, numbers, spaces, hyphens, apostrophes
                .replaceAll("[^\\p{L}\\p{N}\\s\\-'.]", "")
                // Replace multiple spaces with single space
                .replaceAll("\\s+", " ")
                .trim();

        // If name becomes empty after sanitization, use a default
        if (sanitized.isEmpty()) {
            sanitized = "User";
        }

        // Limit length to avoid issues
        if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 50).trim();
        }

        log.info("🧹 Sanitized name: '{}' -> '{}'", name, sanitized);
        return sanitized;
    }

    private String generateUniqueUsername(String email, Keycloak keycloak) {
        // Start with email as username
        String baseUsername = email;
        String username = baseUsername;
        int counter = 1;

        // Check if username exists, if yes, append counter
        while (usernameExists(username, keycloak)) {
            username = baseUsername + "." + counter;
            counter++;

            // Prevent infinite loop
            if (counter > 100) {
                username = baseUsername + "." + UUID.randomUUID().toString().substring(0, 8);
                break;
            }
        }

        log.info("🔍 Generated unique username: {} for email: {}", username, email);
        return username;
    }

    private boolean usernameExists(String username, Keycloak keycloak) {
        try {
            List<UserRepresentation> users = keycloak.realm(realm)
                    .users()
                    .search(username, null, null, null, 0, 1);

            // Check if any user has exact username match
            return users.stream().anyMatch(user -> username.equals(user.getUsername()));
        } catch (Exception e) {
            log.warn("Error checking username existence: {}", e.getMessage());
            return false;
        }
    }

    private String findExistingUserByEmail(String email, Keycloak keycloak) {
        try {
            List<UserRepresentation> users = keycloak.realm(realm)
                    .users()
                    .search(null, null, null, email, 0, 10);

            for (UserRepresentation user : users) {
                if (email.equalsIgnoreCase(user.getEmail())) {
                    log.info("✅ Found existing user by email: {} with ID: {}", email, user.getId());

                    // Update user in local database if needed
                    saveGoogleUserToDatabase(email, user.getFirstName(), user.getLastName(), user.getId());

                    return user.getId();
                }
            }

            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error finding existing user: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    private TokenExchangeResponse generateUserTokenDirectly(String userId) {
        try {
            log.info("🎯 Generating user token via token exchange...");

            // Step 1: Get admin token
            String adminToken = getAdminToken();

            // Step 2: Get user info to extract username
            String username = getUsernameById(userId);
            if (username == null) {
                log.error("❌ Could not get username for userId: {}", userId);
                throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS);
            }

            // Step 3: Use token exchange with admin privileges
            return exchangeTokenForUser(adminToken, username);

        } catch (Exception e) {
            log.error("❌ Direct token generation failed: {}", e.getMessage());
            throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }

    private String getUsernameById(String userId) {
        Keycloak keycloak = null;
        try {
            keycloak = keycloakService.getKeyCloakInstance();
            UserRepresentation user = keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .toRepresentation();

            log.info("📋 Retrieved user: {} with username: {}", user.getEmail(), user.getUsername());
            return user.getUsername();

        } catch (Exception e) {
            log.error("❌ Error getting user by ID: {}", e.getMessage());
            return null;
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    private TokenExchangeResponse exchangeTokenForUser(String adminToken, String username) {
        try {
            log.info("🔄 Exchanging admin token for user token...");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("subject_token", adminToken);
            params.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
            params.add("requested_subject", username);
            params.add("audience", clientId);

            TokenExchangeResponse response = identityClient.exchangeToken(params);
            log.info("✅ Successfully exchanged token for user: {}", username);
            return response;

        } catch (Exception e) {
            log.error("❌ Token exchange failed: {}", e.getMessage());

            // Fallback: Try direct password grant if we have stored credentials
            return createTemporaryUserSession(username);
        }
    }

    private TokenExchangeResponse createTemporaryUserSession(String username) {
        try {
            log.info("🔄 Creating temporary session for user: {}", username);

            // Generate a temporary password
            String tempPassword = generateTemporaryPassword();

            // Set temporary password for user
            setTemporaryPasswordForUser(username, tempPassword);

            // Login with temporary password
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "password");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("username", username);
            params.add("password", tempPassword);

            TokenExchangeResponse response = identityClient.exchangeToken(params);

            // Remove temporary password
            removePasswordForUser(username);

            log.info("✅ Successfully created session for user: {}", username);
            return response;

        } catch (Exception e) {
            log.error("❌ Temporary session creation failed: {}", e.getMessage());
            throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private void setTemporaryPasswordForUser(String username, String tempPassword) {
        Keycloak keycloak = null;
        try {
            keycloak = keycloakService.getKeyCloakInstance();

            List<UserRepresentation> users = keycloak.realm(realm)
                    .users()
                    .search(username);

            if (!users.isEmpty()) {
                String userId = users.get(0).getId();
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(tempPassword);
                credential.setTemporary(false);

                keycloak.realm(realm)
                        .users()
                        .get(userId)
                        .resetPassword(credential);

                log.info("🔑 Set temporary password for user: {}", username);
            }
        } catch (Exception e) {
            log.error("❌ Error setting temporary password: {}", e.getMessage());
            throw new RuntimeException("Failed to set temporary password", e);
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    private void removePasswordForUser(String username) {
        Keycloak keycloak = null;
        try {
            keycloak = keycloakService.getKeyCloakInstance();

            List<UserRepresentation> users = keycloak.realm(realm)
                    .users()
                    .search(username);

            if (!users.isEmpty()) {
                String userId = users.get(0).getId();

                // Remove password by setting user to not require credentials
                UserRepresentation user = users.get(0);
                user.setCredentials(new ArrayList<>());

                keycloak.realm(realm)
                        .users()
                        .get(userId)
                        .update(user);

                log.info("🗑️ Removed temporary password for user: {}", username);
            }
        } catch (Exception e) {
            log.error("❌ Error removing temporary password: {}", e.getMessage());
            // Don't throw exception here as token was already generated
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    private String getCreatedUserId(Response response) {
        String location = response.getHeaderString("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private String getAdminToken() {
        try {
            MultiValueMap<String, String> adminParams = new LinkedMultiValueMap<>();
            adminParams.add("grant_type", "client_credentials");
            adminParams.add("client_id", clientId);
            adminParams.add("client_secret", clientSecret);

            TokenExchangeResponse adminTokenResponse = identityClient.exchangeToken(adminParams);
            log.info("✅ Retrieved admin token successfully");
            return adminTokenResponse.accessToken();

        } catch (Exception e) {
            log.error("❌ Failed to get admin token: {}", e.getMessage());
            throw new RuntimeException("Failed to get admin token", e);
        }
    }

    private boolean validateCurrentPassword(String username, String oldPassword) {
        try {
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("grant_type", "password");
            requestBody.add("username", username);
            requestBody.add("password", oldPassword);

            // Try to get token with old password
            TokenExchangeResponse response = identityClient.exchangeToken(requestBody);
            return response != null && response.accessToken() != null;

        } catch (FeignException e) {
            if (e.status() == 401) {
                return false; // Invalid password
            }
            log.error("Error validating current password", e);
            return false;
        } catch (Exception e) {
            log.error("Error validating current password", e);
            return false;
        }
    }

    private void updateUserGoogleAttributes(UserRepresentation user, String googleUserId,
                                          String picture, Keycloak keycloak) {
        try {
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }

            attributes.put("googleUserId", Collections.singletonList(googleUserId));
            attributes.put("provider", Collections.singletonList("google"));
            if (picture != null) {
                attributes.put("picture", Collections.singletonList(picture));
            }

            user.setAttributes(attributes);

            // Update user in Keycloak
            keycloak.realm(realm)
                    .users()
                    .get(user.getId())
                    .update(user);

            log.info("✅ Updated user attributes for: {}", user.getEmail());
        } catch (Exception e) {
            log.error("❌ Error updating user attributes: {}", e.getMessage());
            // Don't throw exception here as it's not critical
        }
    }

    private void saveGoogleUserToDatabase(String email, String firstName, String lastName, String userId) {
        try {
            // Check if user already exists in database
            Optional<Account> existingAccount = accountRepository.findByEmail(email);

            if (existingAccount.isEmpty()) {
                Account account = Account.builder()
                        .email(email)
                        .firstName(firstName != null ? firstName : "")
                        .lastName(lastName != null ? lastName : "")
                        .username(email)
                        .userId(userId)
                        .roles(Collections.singleton(Role.USER))
                        .active(true)
                        .build();

                accountRepository.saveAndFlush(account);
                log.info("✅ Saved Google user to database: {}", email);
            } else {
                // Update existing user's userId if needed
                Account account = existingAccount.get();
                if (account.getUserId() == null || !account.getUserId().equals(userId)) {
                    account.setUserId(userId);
                    accountRepository.saveAndFlush(account);
                    log.info("✅ Updated existing user's Keycloak ID: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("❌ Error saving user to database: {}", e.getMessage());
            // Don't throw exception here as Keycloak user is already created
        }
    }
}
