package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterDto;
import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChildCharacterService {

    // Create
    ChildCharacterDto createChildCharacter(ChildCharacterCreateDto createDto);
    ChildCharacterDto getChildCharacterById(Long id);
    List<ChildCharacterDto> getAllChildCharacters();
    Page<ChildCharacterDto> getChildCharactersPaged(Pageable pageable);
    List<ChildCharacterDto> getChildCharactersByChildId(Long childId);
    List<ChildCharacterDto> getFavoriteCharactersByChildId(Long childId);

    // Update
    ChildCharacterDto updateChildCharacter(Long id, ChildCharacterCreateDto updateDto);
    ChildCharacterDto toggleFavorite(Long id);

    // Delete
    void deleteChildCharacter(Long id);
    void deleteChildCharactersByChildId(Long childId);

    // Business logic
    boolean isCharacterUnlockedByChild(Long childId, Long characterId);
    ChildCharacterDto unlockCharacterForChild(Long childId, Long characterId);
}