package exe2.learningapp.logineko.authentication.dtos.account_character;

import jakarta.validation.constraints.*;

public record AccountCharacterCreateDto(
        @NotNull(message = "ID người dùng không được để trống")
        @Positive(message = "ID người dùng phải là số dương")
        Long accountId,

        @NotNull(message = "ID nhân vật không được để trống")
        @Positive(message = "ID nhân vật phải là số dương")
        Long characterId,

        @NotNull(message = "Trạng thái yêu thích phải được chỉ định")
        boolean isFavorite
) {}