package exe2.learningapp.logineko.authentication.dtos.character;

import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record CharacterDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        int starRequired,
        CharacterRarity rarity,
        boolean isPremium,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}