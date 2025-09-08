package exe2.learningapp.logineko.authentication.dtos.child_character;


import jakarta.validation.constraints.*;

public record ChildCharacterCreateDto(
        @NotNull(message = "Child ID is required")
        @Positive(message = "Child ID must be positive")
        Long childId,

        @NotNull(message = "Character ID is required")
        @Positive(message = "Character ID must be positive")
        Long characterId,

        @NotNull(message = "Favorite status must be specified")
        boolean isFavorite
) {}
