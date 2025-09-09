package exe2.learningapp.logineko.authentication.dtos.child_character;


import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ChildCharacterDto(
        Long id,
        Long childId,
        CharacterDto character,
        LocalDateTime unlockedAt,
        boolean isFavorite
) {}