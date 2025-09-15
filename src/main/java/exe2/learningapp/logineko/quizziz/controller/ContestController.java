package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.quizziz.dto.ContestDTO;
import exe2.learningapp.logineko.quizziz.service.ContestService;
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

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Room Management", description = "API quản lý các phòng học (Room)")
public class ContestController {

    private final ContestService contestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo một Room mới",
            description = "Tạo một phòng học mới với tiêu đề và mô tả. Hệ thống sẽ tự động tạo một mã code duy nhất cho Room."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo Room thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin Room không hợp lệ")
    })
    public ApiResponse<ContestDTO.Response> createRoom(
            @Valid @RequestBody ContestDTO.Request create) {
        log.info("Creating a new room with title: {}", create.title());
        ContestDTO.Response room = contestService.create(create);
        return ApiResponse.success(room, "Tạo phòng học thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin Room",
            description = "Cập nhật tiêu đề, mô tả và trạng thái công khai của một phòng học."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật Room thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Room"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<ContestDTO.UpdateRoom> updateRoom(
            @Parameter(description = "ID của Room") @PathVariable Long id,
            @Valid @RequestBody ContestDTO.UpdateRoom update) {
        log.info("Updating room with ID: {}", id);
        ContestDTO.UpdateRoom room = contestService.update(id, update);
        return ApiResponse.success(room, "Cập nhật phòng học thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa một Room",
            description = "Xóa hoàn toàn một phòng học khỏi hệ thống."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa Room thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Room")
    })
    public ApiResponse<Void> deleteRoom(
            @Parameter(description = "ID của Room") @PathVariable Long id) {
        log.info("Deleting room with ID: {}", id);
        contestService.delete(id);
        return ApiResponse.success(null, "Xóa phòng học thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin Room theo ID",
            description = "Lấy thông tin chi tiết của một phòng học cụ thể, bao gồm mã code Room."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin Room thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Room")
    })
    public ApiResponse<ContestDTO.Response> getRoomById(
            @Parameter(description = "ID của Room") @PathVariable Long id) {
        log.info("Getting room by ID: {}", id);
        ContestDTO.Response room = contestService.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return ApiResponse.success(room, "Lấy thông tin phòng học thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả các Room",
            description = "Lấy danh sách tất cả các phòng học trong hệ thống với phân trang và tìm kiếm."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách Room thành công")
    })
    public ApiResponse<Page<ContestDTO.Response>> getAllRooms(
            @Parameter(description = "Từ khóa tìm kiếm (tiêu đề hoặc mô tả)") @RequestParam(required = false) String keyword,
            @Parameter(description = "Thông tin phân trang (page, size, sort)") Pageable pageable) {
        log.info("Getting all rooms with keyword: {}", keyword);
        Page<ContestDTO.Response> rooms = contestService.findAll(keyword, pageable);
        return ApiResponse.success(rooms, "Lấy danh sách phòng học thành công");
    }
}