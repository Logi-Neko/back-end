package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterSearchRequest;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.Character;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import exe2.learningapp.logineko.authentication.repository.CharacterRepository;
import exe2.learningapp.logineko.authentication.mapper.CharacterMapper;
import exe2.learningapp.logineko.authentication.repository.specification.CharacterSpecification;
import exe2.learningapp.logineko.common.dto.PaginatedResponse;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CharacterServiceImpl implements CharacterService {

    private final CharacterRepository characterRepository;
    private final CharacterMapper characterMapper;
    private final FileService fileService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public CharacterDto createCharacter(CharacterCreateDto characterCreateDto, MultipartFile thumbnail) throws IOException {
        log.info("Creating character with name: {}", characterCreateDto.name());
        // Check if character name already exists
        if (characterRepository.existsByName(characterCreateDto.name())) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }

        Character character = characterMapper.toEntity(characterCreateDto);
        Pair<String,String> fileData = fileService.uploadFile(thumbnail, "/character" );
        character.setImageUrl(fileData.getFirst());

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
//        character.setImageUrl(characterCreateDto.imageUrl());
        character.setRarity(characterCreateDto.rarity());
        character.setPremium(characterCreateDto.isPremium());
        character.setStarRequired(characterCreateDto.starRequired());
        character.setActive(characterCreateDto.isActive());

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
    public PaginatedResponse<CharacterDto> searchCharacters(CharacterSearchRequest request) {


        Specification<Character> spec = Specification.allOf(
                CharacterSpecification.hasKeyword(request.getKeyword()),
                CharacterSpecification.isPremium(request.getIsPremium()),
                CharacterSpecification.isActive(request.getIsActive()),
                CharacterSpecification.starRequiredBetween(request.getMinStars(), request.getMaxStars()),
                CharacterSpecification.hasRarity(request.getRarity())
        );
        // Tạo Pageable (có sort)
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(request.getSortDir())
                        ? Sort.Order.desc(request.getSortBy())
                        : Sort.Order.asc(request.getSortBy())
        );

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Thực hiện tìm kiếm và map DTO
        Page<CharacterDto> responsePage = characterRepository.findAll(spec, pageable)
                .map(characterMapper::toDto);

        return new PaginatedResponse<>(responsePage);
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

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDto> getLockedCharactersForCurrentUser() {
        log.info("Getting locked (not unlocked) characters for current user");

        Account currentUser = currentUserProvider.getCurrentUser();
        Long accountId = currentUser.getId();

        List<Character> lockedCharacters = characterRepository.findLockedCharactersByAccountId(accountId);
        return lockedCharacters.stream()
                .map(characterMapper::toDto)
                .toList();
    }
}