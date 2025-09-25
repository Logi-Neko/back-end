package exe2.learningapp.logineko.authentication.dtos.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AccountDTO {

    public record CreateAccountRequest(
            @NotBlank(message = "Tên đăng nhập không được để trống")
            @Size(min = 3, max = 20, message = "Tên đăng nhập phải từ 3 đến 20 ký tự")
            String username,

            @NotBlank(message = "Mật khẩu không được để trống")
            @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
            String password,

            @NotBlank(message = "Email không được để trống")
            @Email(message = "Email không hợp lệ")
            String email,

            @NotBlank(message = "Họ và tên không được để trống")
            @Size(max = 50, message = "Không quá 50 ký tự")
            String fullName

    ) {}

        public record AccountResponse(
                Long id,
                String username,
                String email,
                String fullName,
                LocalDate premiumUntil,
                Boolean premium,
                Long totalStar,
                LocalDate dateOfBirth
        ) {}
    public record LoginRequest(
            @NotBlank(message = "Tên đăng nhập không được để trống")
            String username,

            @NotBlank(message = "Mật khẩu không được để trống")
            String password
    ) {}
}
