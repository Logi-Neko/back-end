package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.ApiResponse;
import exe2.learningapp.logineko.quizziz.dto.QuizDTO;
import exe2.learningapp.logineko.quizziz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Management", description = "API quản lý các bài kiểm tra (Quiz)")
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo một Quiz mới trong một Room",
            description = "Tạo một bài kiểm tra mới và liên kết nó với một Room cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo Quiz thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Room"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin Quiz không hợp lệ")
    })
    public ApiResponse<QuizDTO.Response> createQuiz(
            @Parameter(description = "ID của Room") @PathVariable Long roomId,
            @Valid @RequestBody QuizDTO.Request request) {

        QuizDTO.Response quiz = quizService.createQuiz(request);
        return ApiResponse.success(quiz, "Tạo Quiz thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin Quiz",
            description = "Cập nhật thông tin cơ bản của một bài kiểm tra."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật Quiz thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Quiz"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<QuizDTO.Response> updateQuiz(
            @Parameter(description = "ID của Quiz") @PathVariable Long id,
            @Valid @RequestBody QuizDTO.Request request) {
        log.info("Updating quiz with ID: {}", id);

        QuizDTO.Response quiz = quizService.updateQuiz(id, request);
        return ApiResponse.success(quiz, "Cập nhật Quiz thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin Quiz theo ID",
            description = "Lấy thông tin chi tiết của một bài kiểm tra cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin Quiz thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Quiz")
    })
    public ApiResponse<QuizDTO.Response> getQuizById(
            @Parameter(description = "ID của Quiz") @PathVariable Long id) {
        log.info("Getting quiz by ID: {}", id);

        QuizDTO.Response quiz = quizService.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return ApiResponse.success(quiz, "Lấy thông tin Quiz thành công");
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(
            summary = "Lấy danh sách Quiz theo Room",
            description = "Lấy tất cả các bài kiểm tra thuộc về một Room cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách Quiz thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Room")
    })
    public ApiResponse<List<QuizDTO.Response>> getQuizzesByRoomId(
            @Parameter(description = "ID của Room") @PathVariable Long roomId) {
        log.info("Getting quizzes for room ID: {}", roomId);

        List<QuizDTO.Response> quizzes = quizService.findByRoom(roomId);
        return ApiResponse.success(quizzes, "Lấy danh sách Quiz theo Room thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa một Quiz",
            description = "Xóa hoàn toàn một bài kiểm tra khỏi hệ thống."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa Quiz thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Quiz")
    })
    public ApiResponse<Void> deleteQuiz(
            @Parameter(description = "ID của Quiz") @PathVariable Long id) {
        log.info("Deleting quiz with ID: {}", id);

        quizService.deleteQuiz(id);
        return ApiResponse.success(null, "Xóa Quiz thành công");
    }
}