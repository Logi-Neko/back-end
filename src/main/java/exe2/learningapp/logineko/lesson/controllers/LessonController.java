package exe2.learningapp.logineko.lesson.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.requests.LessonFilterRequest;
import exe2.learningapp.logineko.lesson.dtos.requests.LessonRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.LessonDTO;
import exe2.learningapp.logineko.lesson.services.LessonService;
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
@RequestMapping("/lessons")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "Lesson Management", description = "API quản lý bài học trong hệ thống")
public class LessonController {
    LessonService lessonService;

    @PostMapping
    @Operation(
            summary = "Tạo mới bài học",
            description = "Tạo mới một bài học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo bài học thành công"),
    })
    public ResponseEntity<ApiResponse<LessonDTO>> create(
            @RequestPart @Valid LessonRequest request,
            @RequestPart MultipartFile thumbnail
    ) {
        LessonDTO lessonDTO = lessonService.create(request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(lessonDTO));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Cập nhật bài học",
            description = "Cập nhật một bài học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật bài học thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bài học"),
    })
    public ResponseEntity<ApiResponse<LessonDTO>> update(
            @PathVariable Long id,
            @RequestPart @Valid LessonRequest request,
            @RequestPart(required = false) MultipartFile thumbnail
    ) {
        LessonDTO lessonDTO = lessonService.update(id, request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(lessonDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Xóa bài học",
            description = "Xóa một bài học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa bài học thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bài học"),
    })
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping
    @Operation(
            summary = "Tìm tất cả bài học",
            description = "Tìm tất cả bài học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
    })
    public ResponseEntity<ApiResponse<List<LessonDTO>>> findAll() {
        List<LessonDTO> lessons = lessonService.findAll();
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @GetMapping("/course/{courseId}")
    @Operation(
            summary = "Tìm tất cả bài học theo khóa học",
            description = "Tìm tất cả bài học trong khóa học cụ thể"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khóa học"),
    })
    public ResponseEntity<ApiResponse<List<LessonDTO>>> findByCourseId(
            @PathVariable Long courseId
    ) {
        List<LessonDTO> lessons = lessonService.findByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Tìm bài học cụ thể",
            description = "Tìm bài học cụ thể bằng ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin bài học thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bài học"),
    })
    public ResponseEntity<ApiResponse<LessonDTO>> findById(@PathVariable Long id) {
        LessonDTO lessonDTO = lessonService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(lessonDTO));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Tìm kiếm bài học",
            description = "Tìm kiếm bài học theo nhiều điều kiện"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
    })
    public ResponseEntity<ApiResponse<List<LessonDTO>>> search(@RequestBody LessonFilterRequest request) {
        List<LessonDTO> lessons = lessonService.search(request);
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }
}
