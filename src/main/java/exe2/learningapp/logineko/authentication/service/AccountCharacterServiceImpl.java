package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterSearchRequest;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import exe2.learningapp.logineko.authentication.entity.Character;
import exe2.learningapp.logineko.authentication.entity.AccountCharacter;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.AccountCharacterRepository;
import exe2.learningapp.logineko.authentication.repository.CharacterRepository;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.mapper.AccountCharacterMapper;
import exe2.learningapp.logineko.authentication.repository.specification.AccountCharacterSpecification;
import exe2.learningapp.logineko.authentication.repository.specification.CharacterSpecification;
import exe2.learningapp.logineko.common.dto.PaginatedResponse;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountCharacterServiceImpl implements AccountCharacterService {

    private final AccountCharacterRepository accountCharacterRepository;
    private final CharacterRepository characterRepository;
    private final AccountRepository accountRepository;
    private final AccountCharacterMapper accountCharacterMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public AccountCharacterDto createAccountCharacter(AccountCharacterCreateDto createDto) {

        Account currentUser = currentUserProvider.getCurrentUser();

        Character character = characterRepository.findById(createDto.characterId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        if(!character.isActive()){
            throw new AppException(ErrorCode.ERR_INACTIVE);
        }

        Optional<AccountCharacter> existing = accountCharacterRepository
                .findByAccountIdAndCharacterId(currentUser.getId(), createDto.characterId());
        if (existing.isPresent()) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }
        if(character.isPremium() && !currentUser.getPremium()){
            throw new AppException(ErrorCode.ERR_PREMIUM_CHARACTER);
        }
        if(currentUser.getTotalStar() < character.getStarRequired()){
            throw new AppException(ErrorCode.ERR_INSUFFICIENT_STARS);
        }
        AccountCharacter accountCharacter = AccountCharacter.builder()
                .account(currentUser)
                .character(character)
                .isFavorite(false)
                .unlockedAt(LocalDateTime.now())
                .build();

        AccountCharacter saved = accountCharacterRepository.save(accountCharacter);

        return accountCharacterMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountCharacterDto getAccountCharacterById(Long id) {
        log.info("Getting account character by ID: {}", id);

        AccountCharacter accountCharacter = accountCharacterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return accountCharacterMapper.toDto(accountCharacter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountCharacterDto> getAllAccountCharacters() {
        log.info("Getting all account characters");

        List<AccountCharacter> accountCharacters = accountCharacterRepository.findAll();
        return accountCharacters.stream()
                .map(accountCharacterMapper::toDto)
                .toList();
    }

    @Override
    public void deleteAccountCharacter(Long id) {
        log.info("Deleting account character with ID: {}", id);

        if (!accountCharacterRepository.existsById(id)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        accountCharacterRepository.deleteById(id);
        log.info("Deleted account character with ID: {}", id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AccountCharacterDto> getUnlockedCharactersByAccount() {
        Account currentUser = currentUserProvider.getCurrentUser();
        Long accountId = currentUser.getId();

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        List<AccountCharacter> unlockedCharacters = accountCharacterRepository.findByAccountId(accountId);
        return unlockedCharacters.stream()
                .map(accountCharacterMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountCharacterDto> getFavoriteCharactersByAccountId() {
        Account currentUser = currentUserProvider.getCurrentUser();
        Long accountId = currentUser.getId();

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        List<AccountCharacter> favoriteCharacters = accountCharacterRepository
                .findByAccountIdAndIsFavoriteTrue(accountId);
        return favoriteCharacters.stream()
                .map(accountCharacterMapper::toDto)
                .toList();
    }

    @Override
    public AccountCharacterDto setFavoriteCharacter( Long id, boolean isFavorite) {

        AccountCharacter accountCharacter = accountCharacterRepository
                .findById( id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        accountCharacter.setFavorite(isFavorite);
        AccountCharacter saved = accountCharacterRepository.save(accountCharacter);
        return accountCharacterMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<AccountCharacterDto> searchAccountCharacters(AccountCharacterSearchRequest request) {
        Account currentUser = currentUserProvider.getCurrentUser();
        Long accountId = currentUser.getId();

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        Specification<AccountCharacter> spec = Specification.allOf(
                AccountCharacterSpecification.hasCharacterName(request.getCharacterName()),
                AccountCharacterSpecification.hasCharacterRarity(request.getCharacterRarity()),
                AccountCharacterSpecification.isFavorite(request.getIsFavorite()),
                AccountCharacterSpecification.belongsToAccount(accountId)
        );
        // Tạo Pageable (có sort)
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(request.getSortDir())
                        ? Sort.Order.desc(request.getSortBy())
                        : Sort.Order.asc(request.getSortBy())
        );

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Thực hiện tìm kiếm và map DTO
        Page<AccountCharacterDto> responsePage = accountCharacterRepository.findAll( spec, pageable)
                .map(accountCharacterMapper::toDto);

        return new PaginatedResponse<>(responsePage);
    }

    @Override
    public void chooseCharacter(Long accountCharacterId) {
        try {
            Account currentUser = currentUserProvider.getCurrentUser();
            Long accountId = currentUser.getId();

            AccountCharacter accountCharacter = accountCharacterRepository
                    .findById(accountCharacterId)
                    .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

            if (!accountCharacter.getAccount().getId().equals(accountId)) {
                throw new AppException(ErrorCode.ERR_FORBIDDEN);
            }

            currentUser.setAvatarUrl(accountCharacter.getCharacter().getImageUrl());
            accountRepository.save(currentUser);
        } catch (Exception e) {
            log.error("Error choosing character: {}", e.getMessage());
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }
    }
}