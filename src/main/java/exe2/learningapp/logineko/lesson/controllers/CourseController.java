package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.requests.CourseRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.CourseDTO;
import exe2.learningapp.logineko.lesson.dtos.responses.LessonDTO;
import exe2.learningapp.logineko.lesson.services.CourseService;
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
public class CourseController {
    CourseService courseService;

    @PostMapping
    ResponseEntity<ApiResponse<CourseDTO>> create(
            @RequestPart @Valid CourseRequest request,
            @RequestPart MultipartFile thumbnail
    ) {
        CourseDTO courseDTO = courseService.create(request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(courseDTO));
    }

    @PatchMapping("/{id}")
    ResponseEntity<ApiResponse<CourseDTO>> update(
            @PathVariable Long id,
            @RequestPart @Valid CourseRequest request,
            @RequestPart(required = false) MultipartFile thumbnail
    ) {
        CourseDTO courseDTO = courseService.update(id, request, thumbnail);
        return ResponseEntity.ok(ApiResponse.success(courseDTO));
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<CourseDTO>>> findAll() {
        List<CourseDTO> courses = courseService.findAll();
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CourseDTO>> findById(@PathVariable Long id) {
        CourseDTO courseDTO = courseService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(courseDTO));
    }
}
