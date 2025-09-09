package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.character.CharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import exe2.learningapp.logineko.authentication.entity.Character;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import exe2.learningapp.logineko.authentication.repository.CharacterRepository;
import exe2.learningapp.logineko.authentication.mapper.CharacterMapper;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterServiceImpl implements CharacterService {

    private final CharacterRepository characterRepository;
    private final CharacterMapper characterMapper;

    @Override
    public CharacterDto createCharacter(CharacterCreateDto characterCreateDto) {
        log.info("Creating character with name: {}", characterCreateDto.name());

        // Check if character name already exists
        if (characterRepository.existsByName(characterCreateDto.name())) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }

        Character character = characterMapper.toEntity(characterCreateDto);
        Character saved = characterRepository.save(character);

        log.info("Created character with ID: {} and name: {}", saved.getId(), saved.getName());
        return characterMapper.toDto(saved);
    }

    @Override
    public CharacterDto updateCharacter(Long id, CharacterCreateDto characterCreateDto) {
        log.info("Updating character with ID: {}", id);

        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        // Check if new name conflicts with existing character (excluding current one)
        if (!character.getName().equals(characterCreateDto.name()) &&
                characterRepository.existsByName(characterCreateDto.name())) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }

        // Update character fields
        character.setName(characterCreateDto.name());
        character.setDescription(characterCreateDto.description());
        character.setImageUrl(characterCreateDto.imageUrl());
        character.setRarity(characterCreateDto.rarity());

        Character updated = characterRepository.save(character);
        log.info("Updated character with ID: {} and name: {}", updated.getId(), updated.getName());

        return characterMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterDto getCharacter(Long characterId) {
        log.info("Getting character by ID: {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return characterMapper.toDto(character);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDto> getAllCharacters() {
        log.info("Getting all characters");

        List<Character> characters = characterRepository.findAll();
        return characters.stream()
                .map(characterMapper::toDto)
                .toList();
    }

    @Override
    public CharacterDto deactivateCharacter(Long characterId) {
        log.info("Deactivating character with ID: {}", characterId);

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        character.setActive(false);
        Character updated = characterRepository.save(character);

        log.info("Deactivated character with ID: {} and name: {}", updated.getId(), updated.getName());
        return characterMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDto> getCharactersByRarity(CharacterRarity rarity) {
        log.info("Getting characters by rarity: {}", rarity);

        List<Character> characters = characterRepository.findByRarity(rarity);
        return characters.stream()
                .map(characterMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDto> searchCharactersByName(String keyword) {
        log.info("Searching characters by keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCharacters();
        }

        List<Character> characters = characterRepository.findByNameContainingIgnoreCase(keyword.trim());
        return characters.stream()
                .map(characterMapper::toDto)
                .toList();
    }

    @Override
    public void deleteCharacter(Long characterId) {
        log.info("Deleting character with ID: {}", characterId);

        if (!characterRepository.existsById(characterId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        characterRepository.deleteById(characterId);
        log.info("Deleted character with ID: {}", characterId);
    }
}