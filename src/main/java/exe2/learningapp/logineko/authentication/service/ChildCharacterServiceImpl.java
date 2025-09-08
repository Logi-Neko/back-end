package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class ChildCharacterServiceImpl implements ChildCharacterService{
    @Override
    public ChildCharacterDto createChildCharacter(ChildCharacterCreateDto createDto) {
        return null;
    }

    @Override
    public ChildCharacterDto getChildCharacterById(Long id) {
        return null;
    }

    @Override
    public List<ChildCharacterDto> getAllChildCharacters() {
        return List.of();
    }

    @Override
    public Page<ChildCharacterDto> getChildCharactersPaged(Pageable pageable) {
        return null;
    }

    @Override
    public List<ChildCharacterDto> getChildCharactersByChildId(Long childId) {
        return List.of();
    }

    @Override
    public List<ChildCharacterDto> getFavoriteCharactersByChildId(Long childId) {
        return List.of();
    }

    @Override
    public ChildCharacterDto updateChildCharacter(Long id, ChildCharacterCreateDto updateDto) {
        return null;
    }

    @Override
    public ChildCharacterDto toggleFavorite(Long id) {
        return null;
    }

    @Override
    public void deleteChildCharacter(Long id) {

    }

    @Override
    public void deleteChildCharactersByChildId(Long childId) {

    }

    @Override
    public boolean isCharacterUnlockedByChild(Long childId, Long characterId) {
        return false;
    }

    @Override
    public ChildCharacterDto unlockCharacterForChild(Long childId, Long characterId) {
        return null;
    }
}
