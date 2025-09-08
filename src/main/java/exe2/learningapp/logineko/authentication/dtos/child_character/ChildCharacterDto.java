package exe2.learningapp.logineko.authentication.dtos.child_character;


import java.time.LocalDateTime;

public record ChildCharacterDto(
        Long id,
        Long childId,
        CharacterDto character,
        LocalDateTime unlockedAt,
        boolean isFavorite
) {}