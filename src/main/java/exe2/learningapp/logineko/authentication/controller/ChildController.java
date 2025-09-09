package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.dtos.child.ChildCreateDto;
import exe2.learningapp.logineko.authentication.dtos.child.ChildDto;
import exe2.learningapp.logineko.authentication.service.ChildService;
import exe2.learningapp.logineko.common.ApiResponse;
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
@RequestMapping("/api/children")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Child Management", description = "API quản lý thông tin trẻ em")
public class ChildController {

    private final ChildService childService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo hồ sơ trẻ em mới",
            description = "Tạo mới một hồ sơ trẻ em với thông tin cá nhân chi tiết. Trẻ em sẽ được liên kết với tài khoản phụ huynh."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo hồ sơ trẻ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản phụ huynh"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Tên trẻ đã tồn tại cho phụ huynh này"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin trẻ không hợp lệ")
    })
    public ApiResponse<ChildDto> createChild(
            @Valid @RequestBody ChildCreateDto childCreateDto) {
        log.info("Creating child with name: {} for parent ID: {}",
                childCreateDto.name(), childCreateDto.parentId());

        ChildDto child = childService.createChild(childCreateDto);
        return ApiResponse.success(child, "Tạo hồ sơ trẻ thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin trẻ em",
            description = "Cập nhật thông tin cá nhân của trẻ em như tên, ngày sinh, giới tính và ảnh đại diện"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thông tin trẻ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Tên trẻ đã tồn tại cho phụ huynh này"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<ChildDto> updateChild(
            @Parameter(description = "ID của trẻ em") @PathVariable Long id,
            @Valid @RequestBody ChildCreateDto childUpdateDto) {
        log.info("Updating child with ID: {}", id);

        ChildDto child = childService.updateChild(id, childUpdateDto);
        return ApiResponse.success(child, "Cập nhật thông tin trẻ thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin trẻ em theo ID",
            description = "Lấy thông tin chi tiết của một trẻ em cụ thể dựa trên ID, bao gồm tuổi được tính toán"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin trẻ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em")
    })
    public ApiResponse<ChildDto> getChildById(
            @Parameter(description = "ID của trẻ em") @PathVariable Long id) {
        log.info("Getting child by ID: {}", id);

        ChildDto child = childService.getChildById(id);
        return ApiResponse.success(child, "Lấy thông tin trẻ thành công");
    }

    @GetMapping("/parent/{parentId}")
    @Operation(
            summary = "Lấy danh sách trẻ em theo phụ huynh",
            description = "Lấy tất cả trẻ em thuộc về một phụ huynh cụ thể dựa trên ID tài khoản phụ huynh"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách trẻ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản phụ huynh")
    })
    public ApiResponse<List<ChildDto>> getChildrenByParentId(
            @Parameter(description = "ID của tài khoản phụ huynh") @PathVariable Long parentId) {
        log.info("Getting children for parent ID: {}", parentId);

        List<ChildDto> children = childService.getChildrenByParentId(parentId);
        return ApiResponse.success(children, "Lấy danh sách trẻ theo phụ huynh thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả trẻ em trong hệ thống",
            description = "Lấy danh sách tất cả trẻ em đã đăng ký trong hệ thống, thường dùng cho quản trị viên"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách tất cả trẻ thành công")
    })
    public ApiResponse<List<ChildDto>> getAllChildren() {
        log.info("Getting all children");

        List<ChildDto> children = childService.getAllChildren();
        return ApiResponse.success(children, "Lấy danh sách tất cả trẻ thành công");
    }

    @GetMapping("/age-range")
    @Operation(
            summary = "Lấy trẻ em theo độ tuổi",
            description = "Lấy danh sách trẻ em trong khoảng độ tuổi nhất định, hữu ích cho việc phân nhóm theo lứa tuổi"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy trẻ theo độ tuổi thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Khoảng tuổi không hợp lệ")
    })
    public ApiResponse<List<ChildDto>> getChildrenByAgeRange(
            @Parameter(description = "Tuổi tối thiểu") @RequestParam int minAge,
            @Parameter(description = "Tuổi tối đa") @RequestParam int maxAge) {
        log.info("Getting children by age range: {} to {}", minAge, maxAge);

        List<ChildDto> children = childService.getChildrenByAgeRange(minAge, maxAge);
        return ApiResponse.success(children, "Lấy trẻ theo độ tuổi thành công");
    }

    @PatchMapping("/{id}/image")
    @Operation(
            summary = "Cập nhật ảnh đại diện trẻ em",
            description = "Cập nhật ảnh đại diện cho trẻ em. Hỗ trợ URL ảnh từ các dịch vụ lưu trữ"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật ảnh đại diện thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "URL ảnh không hợp lệ")
    })
    public ApiResponse<ChildDto> updateChildImage(
            @Parameter(description = "ID của trẻ em") @PathVariable Long id,
            @Parameter(description = "URL ảnh mới") @RequestParam String imageUrl) {
        log.info("Updating image for child ID: {} with URL: {}", id, imageUrl);

        ChildDto child = childService.updateChildImage(id, imageUrl);
        return ApiResponse.success(child, "Cập nhật ảnh đại diện thành công");
    }

    @GetMapping("/{id}/exists")
    @Operation(
            summary = "Kiểm tra trẻ em có tồn tại",
            description = "Kiểm tra xem một trẻ em có tồn tại trong hệ thống hay không dựa trên ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra tồn tại thành công")
    })
    public ApiResponse<Boolean> checkChildExists(
            @Parameter(description = "ID của trẻ em") @PathVariable Long id) {
        log.info("Checking if child exists by ID: {}", id);

        boolean exists = childService.existsById(id);
        return ApiResponse.success(exists, "Kiểm tra tồn tại trẻ thành công");
    }

    @GetMapping("/parent/{parentId}/name/{childName}/exists")
    @Operation(
            summary = "Kiểm tra tên trẻ có trùng lặp",
            description = "Kiểm tra xem tên trẻ em đã tồn tại cho một phụ huynh cụ thể hay chưa, tránh trùng lặp tên"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra trùng lặp tên thành công")
    })
    public ApiResponse<Boolean> checkChildExistsByParentAndName(
            @Parameter(description = "ID của phụ huynh") @PathVariable Long parentId,
            @Parameter(description = "Tên trẻ em cần kiểm tra") @PathVariable String childName) {
        log.info("Checking if child exists by parent ID: {} and name: {}", parentId, childName);

        boolean exists = childService.existsByParentAndName(parentId, childName);
        return ApiResponse.success(exists, "Kiểm tra trùng lặp tên trẻ thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa hồ sơ trẻ em",
            description = "Xóa hoàn toàn hồ sơ của một trẻ em khỏi hệ thống. Thao tác này không thể hoàn tác."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa hồ sơ trẻ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy trẻ em")
    })
    public ApiResponse<Void> deleteChild(
            @Parameter(description = "ID của trẻ em") @PathVariable Long id) {
        log.info("Deleting child with ID: {}", id);

        childService.deleteChild(id);
        return ApiResponse.success(null, "Xóa hồ sơ trẻ thành công");
    }

    @DeleteMapping("/parent/{parentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa tất cả trẻ em của phụ huynh",
            description = "Xóa tất cả hồ sơ trẻ em thuộc về một phụ huynh cụ thể. Thường dùng khi xóa tài khoản phụ huynh."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa tất cả trẻ của phụ huynh thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản phụ huynh")
    })
    public ApiResponse<Void> deleteChildrenByParent(
            @Parameter(description = "ID của tài khoản phụ huynh") @PathVariable Long parentId) {
        log.info("Deleting all children for parent ID: {}", parentId);

        childService.deleteChildrenByParent(parentId);
        return ApiResponse.success(null, "Xóa tất cả trẻ của phụ huynh thành công");
    }
}