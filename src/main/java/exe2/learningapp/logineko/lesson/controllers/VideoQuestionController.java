package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.lesson.services.VideoQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video/questions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Video Question Management", description = "API quản lý câu hỏi trong các video bài học trong hệ thống")
@RequiredArgsConstructor
public class VideoQuestionController {
    VideoQuestionService videoQuestionService;

    @PostMapping("/{id}")
    @Operation(
            summary = "Trả lời câu hỏi",
            description = "Trả lời câu hỏi"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công"),
    })
    public ResponseEntity<ApiResponse<Void>> answerQuestion(
            @PathVariable Long id,
            @RequestParam String answer
    ) {
        videoQuestionService.answerQuestion(id, answer);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
