package exe2.learningapp.logineko.authentication.dtos.account;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Tên đăng nhập không được để trống")
        String username
) {
}
