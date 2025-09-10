package exe2.learningapp.logineko.authentication.dtos.child_character;

import jakarta.validation.constraints.*;

public record ChildCharacterCreateDto(
        @NotNull(message = "ID trẻ em không được để trống")
        @Positive(message = "ID trẻ em phải là số dương")
        Long childId,

        @NotNull(message = "ID nhân vật không được để trống")
        @Positive(message = "ID nhân vật phải là số dương")
        Long characterId,

        @NotNull(message = "Trạng thái yêu thích phải được chỉ định")
        boolean isFavorite
) {}