package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterDto;
import exe2.learningapp.logineko.authentication.entity.Character;
import exe2.learningapp.logineko.authentication.entity.Child;
import exe2.learningapp.logineko.authentication.entity.ChildCharacter;
import exe2.learningapp.logineko.authentication.repository.ChildCharacterRepository;
import exe2.learningapp.logineko.authentication.repository.ChildRepository;
import exe2.learningapp.logineko.authentication.repository.CharacterRepository;
import exe2.learningapp.logineko.authentication.mapper.ChildCharacterMapper;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChildCharacterServiceImpl implements ChildCharacterService {

    private final ChildCharacterRepository childCharacterRepository;
    private final ChildRepository childRepository;
    private final CharacterRepository characterRepository;
    private final ChildCharacterMapper childCharacterMapper;

    @Override
    public ChildCharacterDto createChildCharacter(ChildCharacterCreateDto createDto) {
        log.info("Creating child character for childId: {}, characterId: {}",
                createDto.childId(), createDto.characterId());

        Child child = childRepository.findById(createDto.childId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        Character character = characterRepository.findById(createDto.characterId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        Optional<ChildCharacter> existing = childCharacterRepository
                .findByChild_IdAndCharacter_Id(createDto.childId(), createDto.characterId());
        if (existing.isPresent()) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }

        ChildCharacter childCharacter = ChildCharacter.builder()
                .child(child)
                .character(character)
                .isFavorite(createDto.isFavorite())
                .unlockedAt(LocalDateTime.now())
                .build();

        ChildCharacter saved = childCharacterRepository.save(childCharacter);
        log.info("Created child character with ID: {}", saved.getId());

        return childCharacterMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ChildCharacterDto getChildCharacterById(Long id) {
        log.info("Getting child character by ID: {}", id);

        ChildCharacter childCharacter = childCharacterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return childCharacterMapper.toDto(childCharacter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildCharacterDto> getAllChildCharacters() {
        log.info("Getting all child characters");

        List<ChildCharacter> childCharacters = childCharacterRepository.findAll();
        return childCharacters.stream()
                .map(childCharacterMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChildCharacterDto> getChildCharactersPaged(Pageable pageable) {
        log.info("Getting child characters paged: {}", pageable);

        Page<ChildCharacter> childCharacters = childCharacterRepository.findAll(pageable);
        return childCharacters.map(childCharacterMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildCharacterDto> getChildCharactersByChildId(Long childId) {
        log.info("Getting child characters for child ID: {}", childId);

        if (!childRepository.existsById(childId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        List<ChildCharacter> childCharacters = childCharacterRepository.findByChild_Id(childId);
        return childCharacters.stream()
                .map(childCharacterMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildCharacterDto> getFavoriteCharactersByChildId(Long childId) {
        log.info("Getting favorite characters for child ID: {}", childId);

        if (!childRepository.existsById(childId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        List<ChildCharacter> favoriteCharacters = childCharacterRepository
                .findByChild_IdAndIsFavoriteTrue(childId);
        return favoriteCharacters.stream()
                .map(childCharacterMapper::toDto)
                .toList();
    }

    @Override
    public ChildCharacterDto updateChildCharacter(Long id, ChildCharacterCreateDto updateDto) {
        log.info("Updating child character with ID: {}", id);

        ChildCharacter childCharacter = childCharacterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        childCharacter.setFavorite(updateDto.isFavorite());

        ChildCharacter updated = childCharacterRepository.save(childCharacter);
        log.info("Updated child character with ID: {}", updated.getId());

        return childCharacterMapper.toDto(updated);
    }

    @Override
    public ChildCharacterDto toggleFavorite(Long id) {
        log.info("Toggling favorite status for child character ID: {}", id);

        ChildCharacter childCharacter = childCharacterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        childCharacter.setFavorite(!childCharacter.isFavorite());

        ChildCharacter updated = childCharacterRepository.save(childCharacter);
        log.info("Toggled favorite status for child character ID: {}, new status: {}",
                updated.getId(), updated.isFavorite());

        return childCharacterMapper.toDto(updated);
    }

    @Override
    public void deleteChildCharacter(Long id) {
        log.info("Deleting child character with ID: {}", id);

        if (!childCharacterRepository.existsById(id)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        childCharacterRepository.deleteById(id);
        log.info("Deleted child character with ID: {}", id);
    }

    @Override
    public void deleteChildCharactersByChildId(Long childId) {
        log.info("Deleting all child characters for child ID: {}", childId);

        if (!childRepository.existsById(childId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        int deletedCount = childCharacterRepository.deleteByChild_Id(childId);
        log.info("Deleted {} child characters for child ID: {}", deletedCount, childId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCharacterUnlockedByChild(Long childId, Long characterId) {
        log.info("Checking if character {} is unlocked by child {}", characterId, childId);

        return childCharacterRepository.findByChild_IdAndCharacter_Id(childId, characterId)
                .isPresent();
    }

    @Override
    public ChildCharacterDto unlockCharacterForChild(Long childId, Long characterId) {
        log.info("Unlocking character {} for child {}", characterId, childId);

        Optional<ChildCharacter> existing = childCharacterRepository
                .findByChild_IdAndCharacter_Id(childId, characterId);
        if (existing.isPresent()) {
            log.info("Character {} already unlocked for child {}", characterId, childId);
            return childCharacterMapper.toDto(existing.get());
        }

        ChildCharacterCreateDto createDto = new ChildCharacterCreateDto(childId, characterId, false);
        return createChildCharacter(createDto);
    }
}