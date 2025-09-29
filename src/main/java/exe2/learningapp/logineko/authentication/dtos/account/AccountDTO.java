package exe2.learningapp.logineko.authentication.dtos.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Builder;

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
            LocalDate dateOfBirth,
            String avatarUrl
    ) {}
    @Builder
    public record AccountShowResponse(
            Long id,
            String fullName,
            Boolean premium,
            Long totalStar,
            String avatarUrl
    ) {}
    public record LoginRequest(
            @NotBlank(message = "Tên đăng nhập không được để trống")
            String username,

            @NotBlank(message = "Mật khẩu không được để trống")
            String password
    ) {}

    public record UpdateAgeRequest(
            @NotNull(message = "Ngày sinh không được để trống")
            LocalDate dateOfBirth
    ) {}
}
