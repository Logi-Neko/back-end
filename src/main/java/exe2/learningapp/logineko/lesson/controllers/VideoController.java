package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.requests.VideoRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.VideoDTO;
import exe2.learningapp.logineko.lesson.services.VideoService;
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
@RequiredArgsConstructor
public class VideoController {
    VideoService videoService;

    @PostMapping
    public ResponseEntity<ApiResponse<VideoDTO>> create(
            @RequestPart VideoRequest request,
            @RequestPart MultipartFile thumbnail,
            @RequestPart MultipartFile video
    ) {
        VideoDTO videoDTO = videoService.create(request, thumbnail, video);
        return ResponseEntity.ok(ApiResponse.success(videoDTO));
    }

    @PatchMapping("/{id}")
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
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        videoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VideoDTO>>> findByLessonId(@RequestParam Long lessonId) {
        List<VideoDTO> videos = videoService.findByLessonId(lessonId);
        return ResponseEntity.ok(ApiResponse.success(videos));
    }
}
