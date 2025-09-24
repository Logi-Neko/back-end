package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.dtos.account.AccountDTO;
import exe2.learningapp.logineko.authentication.dtos.account.ForgotPasswordRequest;
import exe2.learningapp.logineko.authentication.dtos.account.TokenExchangeResponse;
import exe2.learningapp.logineko.authentication.service.AccountService;
import exe2.learningapp.logineko.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            log.error("Error generating Google login URL", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to generate login URL: " + e.getMessage()));
        }
    }

    @PostMapping("/login/google")
    @Operation(summary = "Login với Google ID token")
    public ResponseEntity<ApiResponse<?>> loginWithGoogleToken(@RequestParam("id_token") String idToken) {
        try {
            log.info("Received Google ID token for authentication");
            TokenExchangeResponse response = accountService.loginGoogle(idToken);
            log.info("response{}", response.accessToken());
            return ResponseEntity.ok(ApiResponse.success(
                    response,
                    "Đăng nhập Google thành công"
            ));
        } catch (Exception e) {
            log.error("Google login failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Google login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Tạo user mới")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody @Valid AccountDTO.CreateAccountRequest request) {
            AccountDTO.AccountResponse newUser = accountService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(newUser));

    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả user")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
//        Authentication authentication =  org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                accountService.getAllUsers(),
                "Lấy danh sách user thành công"
        ));
    }

    @GetMapping("/userinfo")
//    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy thông tin về user đang login")
    public ResponseEntity<ApiResponse<?>> getUserInfo() {
//        Authentication authentication =  org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                accountService.getUserInfo(),
                "Lấy thông tin user thành công"
        ));
    }

    @PostMapping("/login/exchange")
    @Operation(summary = "Loign lấy access token mới")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid AccountDTO.LoginRequest loginRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                accountService.login(loginRequest),
                "Lấy access token thành công"
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
            accountService.sendResetPasswordEmail(request.username());
            return ResponseEntity.ok(ApiResponse.success("Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn"));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Lấy access token mới từ refresh token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestParam("refresh_token") String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success(
                accountService.refreshToken(refreshToken),
                "Lấy access token thành công"
        ));
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất, thu hồi refresh token")
    public ResponseEntity<ApiResponse<?>> logout(@RequestParam("refresh_token") String refreshToken)
    {
        accountService.logout(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @RequestParam("old_password") String oldPassword,
            @RequestParam("new_password") String newPassword) {
        accountService.resetPassword( oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công"));
    }

}
