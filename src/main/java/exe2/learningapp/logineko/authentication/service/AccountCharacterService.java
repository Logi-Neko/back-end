package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountCharacterService {

    // Create
    AccountCharacterDto createChildCharacter(AccountCharacterCreateDto createDto);
    AccountCharacterDto getChildCharacterById(Long id);
    List<AccountCharacterDto> getAllChildCharacters();
    Page<AccountCharacterDto> getChildCharactersPaged(Pageable pageable);
    List<AccountCharacterDto> getChildCharactersByChildId(Long childId);
    List<AccountCharacterDto> getFavoriteCharactersByChildId(Long childId);

    // Update
    AccountCharacterDto updateChildCharacter(Long id, AccountCharacterCreateDto updateDto);
    AccountCharacterDto toggleFavorite(Long id);

    // Delete
    void deleteChildCharacter(Long id);
    void deleteChildCharactersByChildId(Long childId);

    // Business logic
    boolean isCharacterUnlockedByChild(Long childId, Long characterId);
    AccountCharacterDto unlockCharacterForChild(Long childId, Long characterId);
}