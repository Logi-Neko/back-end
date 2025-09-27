package exe2.learningapp.logineko.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterDto;
import exe2.learningapp.logineko.authentication.dtos.character.CharacterSearchRequest;
import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import exe2.learningapp.logineko.authentication.service.CharacterService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Character Management", description = "API quản lý nhân vật trong hệ thống")
public class CharacterController {

    private final CharacterService characterService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo nhân vật mới",
            description = "Tạo một nhân vật mới với thông tin chi tiết như tên, mô tả, độ hiếm và hình ảnh"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin nhân vật không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Tên nhân vật đã tồn tại")
    })

    public ApiResponse<CharacterDto> createCharacter(
            @Parameter(description = "Thông tin nhân vật (JSON)")
            @Valid @RequestPart(value = "characterCreateDto", required = true) String characterCreateDto,
            @Parameter(description = "Hình ảnh nhân vật")
            @RequestPart(value = "characterImage", required = true) MultipartFile characterImage) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CharacterCreateDto dto = objectMapper.readValue(characterCreateDto, CharacterCreateDto.class);
        log.info("Creating character with name: {}", dto.name());
        CharacterDto character = characterService.createCharacter(dto,characterImage);
        return ApiResponse.success(character, "Tạo nhân vật thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin nhân vật",
            description = "Cập nhật toàn bộ thông tin của nhân vật bao gồm tên, mô tả, độ hiếm và hình ảnh"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<CharacterDto> updateCharacter(
            @Parameter(description = "ID của nhân vật") @PathVariable Long id,
            @Valid @RequestBody CharacterCreateDto characterCreateDto) {
        log.info("Updating character with ID: {}", id);

        CharacterDto character = characterService.updateCharacter(id, characterCreateDto);
        return ApiResponse.success(character, "Cập nhật nhân vật thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin nhân vật theo ID",
            description = "Lấy thông tin chi tiết của một nhân vật cụ thể dựa trên ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật")
    })
    public ApiResponse<CharacterDto> getCharacter(
            @Parameter(description = "ID của nhân vật") @PathVariable Long id) {
        log.info("Getting character by ID: {}", id);

        CharacterDto character = characterService.getCharacter(id);
        return ApiResponse.success(character, "Lấy thông tin nhân vật thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả nhân vật",
            description = "Lấy danh sách tất cả nhân vật có trong hệ thống, bao gồm cả nhân vật đã bị vô hiệu hóa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhân vật thành công")
    })
    public ApiResponse<List<CharacterDto>> getAllCharacters() {
        log.info("Getting all characters");

        List<CharacterDto> characters = characterService.getAllCharacters();
        return ApiResponse.success(characters, "Lấy danh sách tất cả nhân vật thành công");
    }

    @GetMapping("/rarity/{rarity}")
    @Operation(
            summary = "Lấy nhân vật theo độ hiếm",
            description = "Lấy danh sách nhân vật theo độ hiếm cụ thể (COMMON, RARE, EPIC, LEGENDARY)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy nhân vật theo độ hiếm thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Độ hiếm không hợp lệ")
    })
    public ApiResponse<List<CharacterDto>> getCharactersByRarity(
            @Parameter(description = "Độ hiếm của nhân vật (COMMON, RARE, EPIC, LEGENDARY)")
            @PathVariable CharacterRarity rarity) {
        log.info("Getting characters by rarity: {}", rarity);

        List<CharacterDto> characters = characterService.getCharactersByRarity(rarity);
        return ApiResponse.success(characters, "Lấy nhân vật theo độ hiếm thành công");
    }

    @GetMapping("/search")
    @Operation(
            summary = "Tìm kiếm nhân vật",
            description = "Tìm kiếm nhân vật theo các tiêu chí như tên, độ hiếm, trạng thái premium, số sao..."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm kiếm nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Tham số tìm kiếm không hợp lệ")
    })
    public ApiResponse<PaginatedResponse<CharacterDto>> searchCharacters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CharacterRarity rarity,
            @RequestParam(required = false) Boolean isPremium,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false) Integer maxStars,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Searching characters with keyword: {}, rarity: {}", keyword, rarity);

        CharacterSearchRequest request = new CharacterSearchRequest();
        request.setKeyword(keyword);
        request.setRarity(rarity);
        request.setIsPremium(isPremium);
        request.setIsActive(isActive);
        request.setMinStars(minStars);
        request.setMaxStars(maxStars);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDir(sortDir);

        PaginatedResponse<CharacterDto> result = characterService.searchCharacters(request);
        return ApiResponse.success(result, "Tìm kiếm nhân vật thành công");
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(
            summary = "Vô hiệu hóa nhân vật",
            description = "Vô hiệu hóa nhân vật thay vì xóa hoàn toàn. Nhân vật vẫn tồn tại trong hệ thống nhưng không hiển thị cho người dùng"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vô hiệu hóa nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Nhân vật đã được vô hiệu hóa trước đó")
    })
    public ApiResponse<CharacterDto> deactivateCharacter(
            @Parameter(description = "ID của nhân vật") @PathVariable Long id) {
        log.info("Deactivating character with ID: {}", id);

        CharacterDto character = characterService.deactivateCharacter(id);
        return ApiResponse.success(character, "Vô hiệu hóa nhân vật thành công");
    }

    @GetMapping("/locked")
    @Operation(
            summary = "Lấy nhân vật chưa mở khóa của user hiện tại",
            description = "Lấy danh sách tất cả các nhân vật mà user hiện tại chưa mở khóa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhân vật chưa mở khóa thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ApiResponse<List<CharacterDto>> getLockedCharactersForCurrentUser() {
        log.info("Getting locked characters for current user");
        
        List<CharacterDto> lockedCharacters = characterService.getLockedCharactersForCurrentUser();
        return ApiResponse.success(lockedCharacters, "Lấy danh sách nhân vật chưa mở khóa thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa nhân vật vĩnh viễn",
            description = "Xóa hoàn toàn nhân vật khỏi hệ thống. Thao tác này không thể hoàn tác và sẽ ảnh hưởng đến tất cả trẻ em đã mở khóa nhân vật này"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Không thể xóa nhân vật đang được sử dụng")
    })
    public ApiResponse<Void> deleteCharacter(
            @Parameter(description = "ID của nhân vật") @PathVariable Long id) {
        log.info("Deleting character with ID: {}", id);

        characterService.deleteCharacter(id);
        return ApiResponse.success(null, "Xóa nhân vật thành công");
    }
}