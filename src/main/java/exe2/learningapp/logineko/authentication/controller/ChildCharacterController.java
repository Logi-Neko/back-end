package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterCreateDto;
import exe2.learningapp.logineko.authentication.dtos.child_character.ChildCharacterDto;
import exe2.learningapp.logineko.authentication.service.ChildCharacterService;
import exe2.learningapp.logineko.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/child-characters")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Child Character Management", description = "API quản lý nhân vật của trẻ em")
public class ChildCharacterController {

    private final ChildCharacterService childCharacterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo nhân vật cho trẻ em",
            description = "Tạo mới một nhân vật cho trẻ em với thông tin chi tiết. Nhân vật sẽ được mở khóa tự động cho trẻ."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em hoặc nhân vật"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nhân vật đã tồn tại cho trẻ này")
    })
    public exe2.learningapp.logineko.common.ApiResponse<ChildCharacterDto> createChildCharacter(
            @Valid @RequestBody ChildCharacterCreateDto createDto) {
        log.info("Creating child character for childId: {}, characterId: {}",
                createDto.childId(), createDto.characterId());

        ChildCharacterDto childCharacter = childCharacterService.createChildCharacter(createDto);
        return ApiResponse.success(childCharacter, "Tạo nhân vật cho trẻ thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin nhân vật của trẻ",
            description = "Lấy thông tin chi tiết của một nhân vật cụ thể thuộc về trẻ em dựa trên ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật của trẻ")
    })
    public ApiResponse<ChildCharacterDto> getChildCharacterById(
            @Parameter(description = "ID của nhân vật trẻ em") @PathVariable Long id) {
        log.info("Getting child character by ID: {}", id);

        ChildCharacterDto childCharacter = childCharacterService.getChildCharacterById(id);
        return ApiResponse.success(childCharacter, "Lấy thông tin nhân vật thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả nhân vật của trẻ em",
            description = "Lấy danh sách tất cả các nhân vật của tất cả trẻ em trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    public ApiResponse<List<ChildCharacterDto>> getAllChildCharacters() {
        log.info("Getting all child characters");

        List<ChildCharacterDto> childCharacters = childCharacterService.getAllChildCharacters();
        return ApiResponse.success(childCharacters, "Lấy danh sách nhân vật thành công");
    }

    @GetMapping("/paged")
    @Operation(
            summary = "Lấy nhân vật trẻ em theo trang",
            description = "Lấy danh sách nhân vật của trẻ em với phân trang để hiển thị hiệu quả trên giao diện"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách phân trang thành công")
    })
    public ApiResponse<Page<ChildCharacterDto>> getChildCharactersPaged(
            @Parameter(description = "Thông tin phân trang") Pageable pageable) {
        log.info("Getting child characters paged: {}", pageable);

        Page<ChildCharacterDto> childCharacters = childCharacterService.getChildCharactersPaged(pageable);
        return ApiResponse.success(childCharacters, "Lấy danh sách nhân vật phân trang thành công");
    }

    @GetMapping("/child/{childId}")
    @Operation(
            summary = "Lấy tất cả nhân vật của một trẻ",
            description = "Lấy danh sách tất cả các nhân vật mà một trẻ em cụ thể đã mở khóa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhân vật của trẻ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em")
    })
    public ApiResponse<List<ChildCharacterDto>> getChildCharactersByChildId(
            @Parameter(description = "ID của trẻ em") @PathVariable Long childId) {
        log.info("Getting child characters for child ID: {}", childId);

        List<ChildCharacterDto> childCharacters = childCharacterService.getChildCharactersByChildId(childId);
        return ApiResponse.success(childCharacters, "Lấy danh sách nhân vật của trẻ thành công");
    }

    @GetMapping("/child/{childId}/favorites")
    @Operation(
            summary = "Lấy nhân vật yêu thích của trẻ",
            description = "Lấy danh sách tất cả các nhân vật mà trẻ em đã đánh dấu là yêu thích"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhân vật yêu thích thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em")
    })
    public ApiResponse<List<ChildCharacterDto>> getFavoriteCharactersByChildId(
            @Parameter(description = "ID của trẻ em") @PathVariable Long childId) {
        log.info("Getting favorite characters for child ID: {}", childId);

        List<ChildCharacterDto> favoriteCharacters = childCharacterService.getFavoriteCharactersByChildId(childId);
        return ApiResponse.success(favoriteCharacters, "Lấy danh sách nhân vật yêu thích thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật nhân vật của trẻ",
            description = "Cập nhật thông tin nhân vật của trẻ em, chủ yếu là trạng thái yêu thích"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật của trẻ")
    })
    public ApiResponse<ChildCharacterDto> updateChildCharacter(
            @Parameter(description = "ID của nhân vật trẻ em") @PathVariable Long id,
            @Valid @RequestBody ChildCharacterCreateDto updateDto) {
        log.info("Updating child character with ID: {}", id);

        ChildCharacterDto childCharacter = childCharacterService.updateChildCharacter(id, updateDto);
        return ApiResponse.success(childCharacter, "Cập nhật nhân vật thành công");
    }

    @PatchMapping("/{id}/toggle-favorite")
    @Operation(
            summary = "Bật/tắt nhân vật yêu thích",
            description = "Chuyển đổi trạng thái yêu thích của nhân vật. Nếu đang yêu thích thì bỏ yêu thích và ngược lại."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thay đổi trạng thái yêu thích thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật của trẻ")
    })
    public ApiResponse<ChildCharacterDto> toggleFavorite(
            @Parameter(description = "ID của nhân vật trẻ em") @PathVariable Long id) {
        log.info("Toggling favorite status for child character ID: {}", id);

        ChildCharacterDto childCharacter = childCharacterService.toggleFavorite(id);
        return ApiResponse.success(childCharacter, "Thay đổi trạng thái yêu thích thành công");
    }

    @GetMapping("/child/{childId}/character/{characterId}/unlocked")
    @Operation(
            summary = "Kiểm tra nhân vật đã mở khóa",
            description = "Kiểm tra xem một nhân vật cụ thể đã được mở khóa bởi trẻ em hay chưa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra thành công")
    })
    public ApiResponse<Boolean> isCharacterUnlockedByChild(
            @Parameter(description = "ID của trẻ em") @PathVariable Long childId,
            @Parameter(description = "ID của nhân vật") @PathVariable Long characterId) {
        log.info("Checking if character {} is unlocked by child {}", characterId, childId);

        boolean isUnlocked = childCharacterService.isCharacterUnlockedByChild(childId, characterId);
        return ApiResponse.success(isUnlocked, "Kiểm tra trạng thái mở khóa thành công");
    }

    @PostMapping("/child/{childId}/character/{characterId}/unlock")
    @Operation(
            summary = "Mở khóa nhân vật cho trẻ",
            description = "Mở khóa một nhân vật cụ thể cho trẻ em. Nếu đã mở khóa rồi thì trả về thông tin hiện tại."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mở khóa thành công hoặc đã mở khóa trước đó"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em hoặc nhân vật")
    })
    public ApiResponse<ChildCharacterDto> unlockCharacterForChild(
            @Parameter(description = "ID của trẻ em") @PathVariable Long childId,
            @Parameter(description = "ID của nhân vật") @PathVariable Long characterId) {
        log.info("Unlocking character {} for child {}", characterId, childId);

        ChildCharacterDto childCharacter = childCharacterService.unlockCharacterForChild(childId, characterId);
        return ApiResponse.success(childCharacter, "Mở khóa nhân vật thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa nhân vật của trẻ",
            description = "Xóa một nhân vật cụ thể khỏi danh sách nhân vật của trẻ em"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhân vật của trẻ")
    })
    public ApiResponse<Void> deleteChildCharacter(
            @Parameter(description = "ID của nhân vật trẻ em") @PathVariable Long id) {
        log.info("Deleting child character with ID: {}", id);

        childCharacterService.deleteChildCharacter(id);
        return ApiResponse.success(null, "Xóa nhân vật thành công");
    }

    @DeleteMapping("/child/{childId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa tất cả nhân vật của trẻ",
            description = "Xóa tất cả các nhân vật mà một trẻ em cụ thể đã mở khóa"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa tất cả nhân vật thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em")
    })
    public ApiResponse<Void> deleteChildCharactersByChildId(
            @Parameter(description = "ID của trẻ em") @PathVariable Long childId) {
        log.info("Deleting all child characters for child ID: {}", childId);

        childCharacterService.deleteChildCharactersByChildId(childId);
        return ApiResponse.success(null, "Xóa tất cả nhân vật của trẻ thành công");
    }
}