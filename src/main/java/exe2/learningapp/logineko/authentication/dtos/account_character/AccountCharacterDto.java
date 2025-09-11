package exe2.learningapp.logineko.authentication.dtos.account_character;


import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record AccountCharacterDto(
        Long id,
        Long accountId,
        CharacterDto character,
        LocalDateTime unlockedAt,
        boolean isFavorite
) {}