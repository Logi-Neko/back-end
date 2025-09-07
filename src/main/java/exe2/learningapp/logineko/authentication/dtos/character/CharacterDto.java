package exe2.learningapp.logineko.authentication.dtos.character;

import java.time.LocalDateTime;

public record CharacterDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        int starRequired,
        boolean isPremium,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}