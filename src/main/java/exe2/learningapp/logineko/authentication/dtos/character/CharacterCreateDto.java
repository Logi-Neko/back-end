package exe2.learningapp.logineko.authentication.dtos.character;

import jakarta.validation.constraints.*;

public record CharacterCreateDto(
        @NotBlank(message = "Character name is required")
        @Size(min = 1, max = 100, message = "Character name must be between 1 and 100 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotBlank(message = "Image URL is required")
        @Pattern(regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp)$",
                message = "Image URL must be a valid HTTP/HTTPS URL ending with image extension")
        String imageUrl,

        @Min(value = 0, message = "Star required must be at least 0")
        @Max(value = 1000, message = "Star required must not exceed 1000")
        int starRequired,

        @NotNull(message = "Premium status must be specified")
        boolean isPremium,

        @NotNull(message = "Active status must be specified")
        boolean isActive
) {}
