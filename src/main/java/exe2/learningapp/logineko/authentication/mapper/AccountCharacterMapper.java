package exe2.learningapp.logineko.authentication.mapper;

import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterDto;
import exe2.learningapp.logineko.authentication.entity.AccountCharacter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountCharacterMapper {

    private final CharacterMapper characterMapper;

    public AccountCharacterDto toDto(AccountCharacter childCharacter) {
        if (childCharacter == null) {
            return null;
        }

        return AccountCharacterDto.builder()
                .id(childCharacter.getId())
                .accountId(childCharacter.getAccount().getId())
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