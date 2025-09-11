package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.requests.LessonFilterRequest;
import exe2.learningapp.logineko.lesson.dtos.requests.LessonRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.LessonDTO;
import exe2.learningapp.logineko.lesson.services.LessonService;
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
public class LessonController {
    LessonService lessonService;

    @PostMapping
    public ResponseEntity<ApiResponse<LessonDTO>> create(
            @RequestPart @Valid LessonRequest request,
            @RequestPart MultipartFile thumbnail
    ) {
        LessonDTO lessonDTO = lessonService.create(request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(lessonDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonDTO>> update(
            @PathVariable Long id,
            @RequestPart @Valid LessonRequest request,
            @RequestPart(required = false) MultipartFile thumbnail
    ) {
        LessonDTO lessonDTO = lessonService.update(id, request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(lessonDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LessonDTO>>> findAll() {
        List<LessonDTO> lessons = lessonService.findAll();
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<LessonDTO>>> findByCourseId(
            @PathVariable Long courseId
    ) {
        List<LessonDTO> lessons = lessonService.findByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonDTO>> findById(@PathVariable Long id) {
        LessonDTO lessonDTO = lessonService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(lessonDTO));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LessonDTO>>> search(@RequestBody LessonFilterRequest request) {
        List<LessonDTO> lessons = lessonService.search(request);
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }
}
