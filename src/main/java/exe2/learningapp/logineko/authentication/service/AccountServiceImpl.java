package exe2.learningapp.logineko.authentication.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                    .password(request.password()) // TODO: hash password
                    .roles(Collections.singleton(Role.USER))
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
                throw new AppException(ErrorCode.ERR_NOT_FOUND);
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
        try (Keycloak keycloak = keycloakService.getKeyCloakInstance()) {
            Account currentUser =  currentUserProvider.getCurrentUser();
            String username = currentUser.getUsername();
            if (!validateCurrentPassword(username, oldPassword)) {
                throw new AppException(ErrorCode.AUTH_INVALID_PASSWORD);
            }

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
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }

    private AccountDTO.AccountResponse mapToDTO(Account account) {
        return new AccountDTO.AccountResponse(
                account.getId(),
                account.getEmail(),
                account.getLastName(),
                account.getUsername()
        );
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
}
