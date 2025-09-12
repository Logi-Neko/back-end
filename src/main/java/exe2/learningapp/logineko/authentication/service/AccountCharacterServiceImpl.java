package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterDto;
import exe2.learningapp.logineko.authentication.entity.Character;
import exe2.learningapp.logineko.authentication.entity.AccountCharacter;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.AccountCharacterRepository;
import exe2.learningapp.logineko.authentication.repository.CharacterRepository;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.mapper.AccountCharacterMapper;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Optional<AccountCharacter> existing = accountCharacterRepository
                .findByAccountIdAndCharacterId(currentUser.getId(), createDto.characterId());
        if (existing.isPresent()) {
            throw new AppException(ErrorCode.ERR_EXISTS);
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
    public List<AccountCharacterDto> searchAccountCharacters(String searchTerm) {
        Account currentUser = currentUserProvider.getCurrentUser();
        Long accountId = currentUser.getId();

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        List<AccountCharacter> accountCharacters = accountCharacterRepository.findByAccountId(accountId);

        return accountCharacters.stream()
                .filter(ac -> ac.getCharacter().getName().toLowerCase()
                        .contains(searchTerm.toLowerCase()))
                .map(accountCharacterMapper::toDto)
                .toList();
    }
}