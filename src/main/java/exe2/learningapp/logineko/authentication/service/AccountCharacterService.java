package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterSearchRequest;
import exe2.learningapp.logineko.common.dto.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountCharacterService {

    // CRUD Operations
    AccountCharacterDto createAccountCharacter(AccountCharacterCreateDto createDto);

    AccountCharacterDto getAccountCharacterById(Long id);

    List<AccountCharacterDto> getAllAccountCharacters();

    void deleteAccountCharacter(Long id);

    List<AccountCharacterDto> getUnlockedCharactersByAccount();
    // Favorite management
    List<AccountCharacterDto> getFavoriteCharactersByAccountId();

    AccountCharacterDto setFavoriteCharacter( Long id, boolean isFavorite);

    PaginatedResponse<AccountCharacterDto> searchAccountCharacters(AccountCharacterSearchRequest searchTerm );
}