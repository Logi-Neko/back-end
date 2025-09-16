package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.requests.CourseRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.CourseDTO;
import exe2.learningapp.logineko.lesson.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/courses")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "API quản lý khóa học trong hệ thống")
public class CourseController {
    CourseService courseService;

    @PostMapping
    @Operation(
            summary = "Tạo mới khóa học",
            description = "Tạo mới một khóa học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo khóa học thành công"),
    })
    public ResponseEntity<ApiResponse<CourseDTO>> create(
            @RequestPart @Valid CourseRequest request,
            @RequestPart MultipartFile thumbnail
    ) {
        CourseDTO courseDTO = courseService.create(request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(courseDTO));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Cập nhật khóa học",
            description = "Cập nhật một khóa học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật khóa học thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khóa học"),
    })
    public ResponseEntity<ApiResponse<CourseDTO>> update(
            @PathVariable Long id,
            @RequestPart @Valid CourseRequest request,
            @RequestPart(required = false) MultipartFile thumbnail
    ) {
        CourseDTO courseDTO = courseService.update(id, request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(courseDTO));
    }

    @GetMapping
    @Operation(
            summary = "Tìm tất cả khóa học",
            description = "Tìm tất cả khóa học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
    })
    public ResponseEntity<ApiResponse<List<CourseDTO>>> findAll() {
        List<CourseDTO> courses = courseService.findAll();
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin khóa học cụ thể",
            description = "Lấy thông tin khóa học cụ thể dựa trên ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
    })
    public ResponseEntity<ApiResponse<CourseDTO>> findById(@PathVariable Long id) {
        CourseDTO courseDTO = courseService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(courseDTO));
    }
}
