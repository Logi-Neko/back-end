package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterDto;
import exe2.learningapp.logineko.authentication.dtos.account_character.AccountCharacterSearchRequest;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import exe2.learningapp.logineko.authentication.service.AccountCharacterService;
import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.common.dto.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-characters")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Character Management", description = "API quản lý nhân vật của tài khoản")
public class AccountCharacterController {

    private final AccountCharacterService accountCharacterService;

    @PostMapping("/unlocked")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo nhân vật cho tài khoản",
            description = "Tạo mới một nhân vật cho tài khoản với thông tin chi tiết. Nhân vật sẽ được mở khóa tự động."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản hoặc nhân vật"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nhân vật đã tồn tại cho tài khoản này")
    })
    public ApiResponse<AccountCharacterDto> createAccountCharacter(
            @Valid @RequestBody AccountCharacterCreateDto createDto) {

        AccountCharacterDto accountCharacter = accountCharacterService.createAccountCharacter(createDto);
        return ApiResponse.success(accountCharacter, "Tạo nhân vật cho tài khoản thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin nhân vật của tài khoản",
            description = "Lấy thông tin chi tiết của một nhân vật cụ thể thuộc về tài khoản dựa trên ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật của tài khoản")
    })
    public ApiResponse<AccountCharacterDto> getAccountCharacterById(
            @Parameter(description = "ID của nhân vật tài khoản") @PathVariable Long id) {
        log.info("Getting account character by ID: {}", id);

        AccountCharacterDto accountCharacter = accountCharacterService.getAccountCharacterById(id);
        return ApiResponse.success(accountCharacter, "Lấy thông tin nhân vật thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả nhân vật của tài khoản",
            description = "Lấy danh sách tất cả các nhân vật của tất cả tài khoản trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    public ApiResponse<List<AccountCharacterDto>> getAllAccountCharacters() {
        log.info("Getting all account characters");

        List<AccountCharacterDto> accountCharacters = accountCharacterService.getAllAccountCharacters();
        return ApiResponse.success(accountCharacters, "Lấy danh sách nhân vật thành công");
    }


    @GetMapping("favorites")
    @Operation(
            summary = "Lấy nhân vật yêu thích của tài khoản",
            description = "Lấy danh sách tất cả các nhân vật mà tài khoản đã đánh dấu là yêu thích"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhân vật yêu thích thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    public ApiResponse<List<AccountCharacterDto>> getFavoriteCharactersByAccountId(
            ) {

        List<AccountCharacterDto> favoriteCharacters = accountCharacterService.getFavoriteCharactersByAccountId();
        return ApiResponse.success(favoriteCharacters, "Lấy danh sách nhân vật yêu thích thành công");
    }

    @GetMapping("/unlocked/all")
    @Operation(
            summary = "Lấy nhân vật đã mở khóa của tài khoản",
            description = "Lấy danh sách tất cả các nhân vật mà tài khoản đã mở khóa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhân vật đã mở khóa thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    public ApiResponse<List<AccountCharacterDto>> getUnlockedCharactersByAccount() {
        List<AccountCharacterDto> unlockedCharacters = accountCharacterService.getUnlockedCharactersByAccount();
        return ApiResponse.success(unlockedCharacters, "Lấy danh sách nhân vật đã mở khóa thành công");
    }

    @PatchMapping("/character/{id}/favorite")
    @Operation(
            summary = "Đặt trạng thái yêu thích cho nhân vật",
            description = "Đặt hoặc bỏ trạng thái yêu thích cho một nhân vật cụ thể"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật trạng thái yêu thích thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật của tài khoản")
    })
    public ApiResponse<AccountCharacterDto> setFavoriteCharacter(
            @Parameter(description = "ID của nhân vật") @PathVariable Long id,
            @Parameter(description = "Trạng thái yêu thích") @RequestParam boolean isFavorite) {

        AccountCharacterDto accountCharacter = accountCharacterService.setFavoriteCharacter( id, isFavorite);
        return ApiResponse.success(accountCharacter, "Cập nhật trạng thái yêu thích thành công");
    }


    @GetMapping("/search")
    @Operation(
            summary = "Tìm kiếm nhân vật của tài khoản",
            description = "Tìm kiếm nhân vật theo tên trong danh sách nhân vật của tài khoản"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm kiếm thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    public ApiResponse<PaginatedResponse<AccountCharacterDto>> searchAccountCharacters(// Required parameter
            @RequestParam(required = false) String characterName,
            @RequestParam(required = false) CharacterRarity characterRarity,
            @RequestParam(required = false) Boolean isFavorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "unlockedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        AccountCharacterSearchRequest request = AccountCharacterSearchRequest.builder()
                .characterName(characterName)
                .characterRarity(characterRarity)
                .isFavorite(isFavorite)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        PaginatedResponse<AccountCharacterDto> result = accountCharacterService.searchAccountCharacters(request);

        return ApiResponse.success(result, "Tìm kiếm nhân vật thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa nhân vật của tài khoản",
            description = "Xóa một nhân vật cụ thể khỏi danh sách nhân vật của tài khoản"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật của tài khoản")
    })
    public ApiResponse<Void> deleteAccountCharacter(
            @Parameter(description = "ID của nhân vật tài khoản") @PathVariable Long id) {
        log.info("Deleting account character with ID: {}", id);

        accountCharacterService.deleteAccountCharacter(id);
        return ApiResponse.success(null, "Xóa nhân vật thành công");
    }
}