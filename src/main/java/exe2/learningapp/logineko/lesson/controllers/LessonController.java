package exe2.learningapp.logineko.lesson.controllers;

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
    ResponseEntity<LessonDTO> create(
            @RequestPart @Valid LessonRequest request,
            @RequestPart MultipartFile thumbnail
    ) {
        return ResponseEntity.ok(lessonService.create(request, thumbnail));
    }

    @PatchMapping("/{id}")
    ResponseEntity<LessonDTO> update(
            @PathVariable Long id,
            @RequestPart @Valid LessonRequest request,
            @RequestPart(required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.ok(lessonService.update(id, request, thumbnail));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<List<LessonDTO>> findAll() {
        return ResponseEntity.ok(lessonService.findAll());
    }

    @GetMapping("/{id}")
    ResponseEntity<LessonDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.findById(id));
    }

    @GetMapping("/search")
    ResponseEntity<List<LessonDTO>> search(@RequestBody LessonFilterRequest request) {
        return ResponseEntity.ok(lessonService.search(request));
    }
}
