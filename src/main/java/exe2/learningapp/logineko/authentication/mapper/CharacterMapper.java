package exe2.learningapp.logineko.authentication.mapper;

import exe2.learningapp.logineko.authentication.dtos.character.CharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import exe2.learningapp.logineko.authentication.entity.Character;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterMapper {

    public CharacterDto toDto(Character character) {
        if (character == null) {
            return null;
        }

        return CharacterDto.builder()
                .id(character.getId())
                .name(character.getName())
                .description(character.getDescription())
                .imageUrl(character.getImageUrl())
                .starRequired(character.getStarRequired())
                .isPremium(character.isPremium())
                .isActive(character.isActive())
                .createdAt(character.getCreatedAt())
                .updatedAt(character.getUpdatedAt())
                .build();
    }

    public Character toEntity(CharacterCreateDto dto) {
        if (dto == null) {
            return null;
        }

        return Character.builder()
                .name(dto.name())
                .description(dto.description())
                .imageUrl(dto.imageUrl())
                .starRequired(dto.starRequired())
                .rarity(dto.rarity())
                .isPremium(dto.isPremium())
                .isActive(dto.isActive())
                .build();
    }
}