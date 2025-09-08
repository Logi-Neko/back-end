package exe2.learningapp.logineko.authentication.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AccountDTO {

    public record CreateAccountRequest(
            @NotBlank(message = "{field_required}")
            @Size(min = 3, max = 20, message = "Tên đăng nhập phải từ 3 đến 20 ký tự")
            String username,

            @NotBlank(message = "{field_required}")
            @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
            String password,

            @NotBlank(message = "{field_required}")
            @Email(message = "{field_invalid}")
            String email,

            @NotBlank(message = "{field_required}")
            @Size(max = 50, message = "Không quá 50 ký tự")
            String firstName,

            @NotBlank(message = "{field_required}")
            @Size(max = 50, message = "Không quá 50 ký tự")
            String lastName
    ) {}

        public record AccountResponse(
            Long id,
            String username,
            String email,
            String firstName,
            String lastName
        ) {}
}
