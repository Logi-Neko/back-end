package exe2.learningapp.logineko.lesson.controllers;

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
    public ResponseEntity<VideoDTO> create(
            @RequestPart VideoRequest request,
            @RequestPart MultipartFile thumbnail,
            @RequestPart MultipartFile video
    ) {
        return ResponseEntity.ok(videoService.create(request, thumbnail, video));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VideoDTO> update(
            @PathVariable Long id,
            @RequestPart VideoRequest request,
            @RequestPart(required = false) MultipartFile thumbnail,
            @RequestPart(required = false) MultipartFile video
    ) {
        return ResponseEntity.ok(videoService.update(id, request, thumbnail, video));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<VideoDTO>> findByLessonId(@RequestParam Long lessonId) {
        return ResponseEntity.ok(videoService.findByLessonId(lessonId));
    }
}
