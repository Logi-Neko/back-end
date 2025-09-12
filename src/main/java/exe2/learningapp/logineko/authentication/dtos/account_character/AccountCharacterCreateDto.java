package exe2.learningapp.logineko.authentication.dtos.account_character;

import jakarta.validation.constraints.*;

public record AccountCharacterCreateDto(

        @NotNull(message = "ID nhân vật không được để trống")
        @Positive(message = "ID nhân vật phải là số dương")
        Long characterId
) {}