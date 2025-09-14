package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.lesson.services.VideoQuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video/questions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VideoQuestionController {
    VideoQuestionService videoQuestionService;

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> answerQuestion(
            @PathVariable Long id,
            @RequestParam String answer
    ) {
        videoQuestionService.answerQuestion(id, answer);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
