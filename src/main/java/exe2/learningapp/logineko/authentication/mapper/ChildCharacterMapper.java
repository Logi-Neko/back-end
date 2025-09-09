package exe2.learningapp.logineko.authentication.mapper;

import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterDto;
import exe2.learningapp.logineko.authentication.entity.ChildCharacter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildCharacterMapper {

    private final ChildMapper childMapper;
    private final CharacterMapper characterMapper;

    public ChildCharacterDto toDto(ChildCharacter childCharacter) {
        if (childCharacter == null) {
            return null;
        }

        return ChildCharacterDto.builder()
                .id(childCharacter.getId())
                .childId(childCharacter.getChild().getId())
                .character(characterMapper.toDto(childCharacter.getCharacter()))
                .isFavorite(childCharacter.isFavorite())
                .unlockedAt(childCharacter.getUnlockedAt())
                .build();
    }

//    public ChildCharacter toEntity(ChildCharacterDto dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        return ChildCharacter.builder()
//                .id(dto.id())
//                .child(childMapper.toEntity(dto.childId()))
//                .character(characterMapper.toEntity(dto.character()))
//                .isFavorite(dto.isFavorite())
//                .unlockedAt(dto.unlockedAt())
//                .build();
//    }
}