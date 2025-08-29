package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.dtos.UserInfo;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.service.AccountService;
import exe2.learningapp.logineko.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.auth-server}")
    private String keycloakUrlWeb;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${app.redirect-uri}")
    private String redirectUri;

    @Value("${app.redirect-uri-web}")
    private String redirectUriWeb;

    private final AccountService accountService;
    // Redirect đến Keycloak với Google hint
//    @GetMapping("/login/google")
//    public ResponseEntity<Void> loginWithGoogle() {
//        String redirectUrl = String.format(
//                "%s/realms/%s/protocol/openid-connect/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=openid&kc_idp_hint=google",
//                keycloakUrl, realm, clientId, redirectUri
//        );
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create(redirectUrl));
//        return new ResponseEntity<>(headers, HttpStatus.FOUND);
//    }
//
//    // Handle callback từ Keycloak
//    @GetMapping("/callback")
//    public ResponseEntity<ApiResponse<?>> handleCallback(
//            @RequestParam("code") String code,
//            @RequestParam(value = "state", required = false) String state) {
//
//        try {
//            // Exchange authorization code for tokens
//            TokenResponse tokens = keycloakService.exchangeCodeForTokens(code);
//
//            // Parse user info from token
//            UserInfo userInfo = keycloakService.getUserInfo(tokens.getAccessToken());
//
//            // Tạo hoặc cập nhật user trong database
//            Account user = accountService.createAccount(userInfo);
//
//            return ResponseEntity.ok(ApiResponse.success(
//                    TokenResponse.builder()
//                            .accessToken(tokens.getAccessToken())
//                            .refreshToken(tokens.getRefreshToken())
//                            .user(userInfo)
//                            .build(),
//                    "Login successful"
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(ApiResponse.error(400, "Login failed: " + e.getMessage()));
//        }
//    }

    @GetMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(
            @RequestParam(value = "device_id", required = false) String deviceId,
            @RequestParam(value = "device_name", required = false) String deviceName) {

        try {
            log.info("Starting Google login flow with device_id: {}, device_name: {}", deviceId, deviceName);

            // Generate state for CSRF protection
            String state = UUID.randomUUID().toString();

            // Build redirect URL với tất cả parameters cần thiết
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(keycloakUrlWeb)
                    .append("/realms/").append(realm)
                    .append("/protocol/openid-connect/auth")
                    .append("?client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8))
                    .append("&redirect_uri=").append(URLEncoder.encode(redirectUriWeb, StandardCharsets.UTF_8))
                    .append("&response_type=code")
                    .append("&scope=").append(URLEncoder.encode("openid email profile", StandardCharsets.UTF_8))
                    .append("&kc_idp_hint=google")
                    .append("&state=").append(state);

            // Thêm device info nếu có
            if (deviceId != null && !deviceId.trim().isEmpty()) {
                urlBuilder.append("&device_id=").append(URLEncoder.encode(deviceId, StandardCharsets.UTF_8));
            }
            if (deviceName != null && !deviceName.trim().isEmpty()) {
                urlBuilder.append("&device_name=").append(URLEncoder.encode(deviceName, StandardCharsets.UTF_8));
            }

            String redirectUrl = urlBuilder.toString();
            log.info("Generated redirect URL: {}", redirectUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            log.error("Error generating login URL", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to generate login URL: " + e.getMessage()));
        }
    }

    @GetMapping("/login/google/url")
    public ResponseEntity<ApiResponse<?>> getGoogleLoginUrl(
            @RequestParam(value = "device_id", required = false) String deviceId,
            @RequestParam(value = "device_name", required = false) String deviceName) {

        try {
            String state = UUID.randomUUID().toString();

            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(keycloakUrl)
                    .append("/realms/").append(realm)
                    .append("/protocol/openid-connect/auth")
                    .append("?client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8))
                    .append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
                    .append("&response_type=code")
                    .append("&scope=").append(URLEncoder.encode("openid email profile", StandardCharsets.UTF_8))
                    .append("&kc_idp_hint=google")
                    .append("&state=").append(state);

            if (deviceId != null && !deviceId.trim().isEmpty()) {
                urlBuilder.append("&device_id=").append(URLEncoder.encode(deviceId, StandardCharsets.UTF_8));
            }
            if (deviceName != null && !deviceName.trim().isEmpty()) {
                urlBuilder.append("&device_name=").append(URLEncoder.encode(deviceName, StandardCharsets.UTF_8));
            }

            return ResponseEntity.ok(ApiResponse.success(
                    Map.of("redirectUrl", urlBuilder.toString(), "state", state),
                    "Login URL generated successfully"
            ));

        } catch (Exception e) {
            log.error("Error generating login URL", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to generate login URL: " + e.getMessage()));
        }
    }

    // Handle callback from Keycloak - cải thiện error handling
//    @GetMapping("/callback")
//    public ResponseEntity<ApiResponse<?>> handleCallback(
//            @RequestParam("code") String code,
//            @RequestParam(value = "state", required = false) String state,
//            @RequestParam(value = "error", required = false) String error,
//            @RequestParam(value = "error_description", required = false) String errorDescription) {
//
//        try {
//            log.info("Handling callback with code: {}, state: {}", code != null ? "present" : "null", state);
//
//            // Check for errors from Keycloak
//            if (error != null) {
//                log.error("Keycloak returned error: {} - {}", error, errorDescription);
//                return ResponseEntity.badRequest()
//                        .body(ApiResponse.error(400, "Authentication failed: " + error + " - " + errorDescription));
//            }
//
//            if (code == null || code.trim().isEmpty()) {
//                return ResponseEntity.badRequest()
//                        .body(ApiResponse.error(400, "Authorization code is missing"));
//            }
//
//            // Exchange authorization code for tokens
//            TokenResponse tokens = keycloakService.exchangeCodeForTokens(code);
//            log.info("Successfully exchanged code for tokens");
//
//            // Parse user info from token
//            UserInfo userInfo = keycloakService.getUserInfo(tokens.getAccessToken());
//            log.info("Retrieved user info for email: {}", userInfo.getEmail());
//
//            // Create or update user in database
//            Account user = accountService.createOrUpdateAccount(userInfo);
//
//            return ResponseEntity.ok(ApiResponse.success(
//                    TokenResponse.builder()
//                            .accessToken(tokens.getAccessToken())
//                            .refreshToken(tokens.getRefreshToken())
//                            .user(userInfo)
//                            .build(),
//                    "Login successful"
//            ));
//
//        } catch (Exception e) {
//            log.error("Error handling callback", e);
//            return ResponseEntity.badRequest()
//                    .body(ApiResponse.error(400, "Login failed: " + e.getMessage()));
//        }
//    }

//    // Endpoint để refresh token
//    @PostMapping("/refresh")
//    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody Map<String, String> request) {
//        try {
//            String refreshToken = request.get("refreshToken");
//            if (refreshToken == null || refreshToken.trim().isEmpty()) {
//                return ResponseEntity.badRequest()
//                        .body(ApiResponse.error(400, "Refresh token is required"));
//            }
//
//            TokenResponse tokens = keycloakService.refreshToken(refreshToken);
//            return ResponseEntity.ok(ApiResponse.success(tokens, "Token refreshed successfully"));
//
//        } catch (Exception e) {
//            log.error("Error refreshing token", e);
//            return ResponseEntity.badRequest()
//                    .body(ApiResponse.error(400, "Token refresh failed: " + e.getMessage()));
//        }
//    }
//
//    // Endpoint để logout
//    @PostMapping("/logout")
//    public ResponseEntity<ApiResponse<?>> logout(@RequestBody Map<String, String> request) {
//        try {
//            String refreshToken = request.get("refreshToken");
//            if (refreshToken != null && !refreshToken.trim().isEmpty()) {
//                keycloakService.logout(refreshToken);
//            }
//            return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
//
//        } catch (Exception e) {
//            log.error("Error during logout", e);
//            return ResponseEntity.ok(ApiResponse.success(null, "Logout completed"));
//        }
//    }
}
