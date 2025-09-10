package exe2.learningapp.logineko.authentication.dtos.child;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ChildCreateDto(
        @NotBlank(message = "Tên trẻ em không được để trống")
        @Size(min = 2, max = 50, message = "Tên trẻ em phải có từ 2-50 ký tự")
        String name,

        @NotNull(message = "Ngày sinh không được để trống")
        @Past(message = "Ngày sinh phải là ngày trong quá khứ")
        LocalDate birthDate,

        @NotBlank(message = "Giới tính không được để trống")
        @Pattern(regexp = "^(Nam|Nữ|Khác)$", message = "Giới tính phải là Nam, Nữ hoặc Khác")
        String gender,

        @Size(max = 255, message = "Đường dẫn hình ảnh không được vượt quá 255 ký tự")
        String imageUrl,

        @NotNull(message = "ID phụ huynh không được để trống")
        Long parentId
) {}