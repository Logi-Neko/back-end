package exe2.learningapp.logineko.authentication.dtos.character;

import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import jakarta.validation.constraints.*;

public record CharacterCreateDto(
        @NotBlank(message = "Tên nhân vật không được để trống")
        @Size(min = 1, max = 100, message = "Tên nhân vật phải có từ 1 đến 100 ký tự")
        String name,

        @NotBlank(message = "Mô tả không được để trống")
        @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
        String description,

        @NotNull(message = "Độ hiếm của nhân vật phải được chỉ định")
        CharacterRarity rarity,

        @NotBlank(message = "URL hình ảnh không được để trống")
        @Pattern(regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp)$",
                message = "URL hình ảnh phải là đường dẫn HTTP/HTTPS hợp lệ và kết thúc bằng định dạng ảnh")
        String imageUrl,

        @Min(value = 0, message = "Số sao yêu cầu phải từ 0 trở lên")
        @Max(value = 1000, message = "Số sao yêu cầu không được vượt quá 1000")
        int starRequired,

        @NotNull(message = "Trạng thái premium phải được chỉ định")
        boolean isPremium,

        @NotNull(message = "Trạng thái hoạt động phải được chỉ định")
        boolean isActive
) {}