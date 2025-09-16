package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.requests.VideoRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.VideoDTO;
import exe2.learningapp.logineko.lesson.services.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/videos")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Video Management", description = "API quản lý video bài học trong hệ thống")
@RequiredArgsConstructor
public class VideoController {
    VideoService videoService;

    @PostMapping
    @Operation(
            summary = "Tạo mới video trong bài học",
            description = "Tạo mới một video trong bài học"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo video bài học thành công"),
    })
    public ResponseEntity<ApiResponse<VideoDTO>> create(
            @RequestPart VideoRequest request,
            @RequestPart MultipartFile thumbnail,
            @RequestPart MultipartFile video
    ) {
        VideoDTO videoDTO = videoService.create(request, thumbnail, video);
        return ResponseEntity.ok(ApiResponse.success(videoDTO));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Cập nhật video trong bài học",
            description = "Cập nhật video trong bài học"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật video bài học thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy video bài học"),
    })
    public ResponseEntity<ApiResponse<VideoDTO>> update(
            @PathVariable Long id,
            @RequestPart VideoRequest request,
            @RequestPart(required = false) MultipartFile thumbnail,
            @RequestPart(required = false) MultipartFile video
    ) {
        VideoDTO videoDTO = videoService.update(id, request, thumbnail, video);
        return ResponseEntity.ok(ApiResponse.success(videoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Xóa video bài học",
            description = "Xóa video bài học trong hệ thống"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa video bài học thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy video bài học"),
    })
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        videoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping
    @Operation(
            summary = "Tìm video bài học trong bài học cụ thể",
            description = "Tìm video bài học trong bài học cụ thể"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy bài học"),
    })
    public ResponseEntity<ApiResponse<List<VideoDTO>>> findByLessonId(@RequestParam Long lessonId) {
        List<VideoDTO> videos = videoService.findByLessonId(lessonId);
        return ResponseEntity.ok(ApiResponse.success(videos));
    }
}
