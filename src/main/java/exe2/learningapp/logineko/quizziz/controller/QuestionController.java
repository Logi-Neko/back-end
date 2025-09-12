package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Management", description = "API quản lý các câu hỏi")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo một câu hỏi mới",
            description = "Tạo một câu hỏi mới và liên kết nó với một Quiz."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo câu hỏi thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Quiz"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin câu hỏi không hợp lệ")
    })
    public ApiResponse<QuestionDTO.Response> createQuestion(
            @Valid @RequestBody QuestionDTO.Request request) {
        log.info("Creating a new question for quiz ID: {}", request.quizId());
        QuestionDTO.Response question = questionService.createQuestion(request);
        return ApiResponse.success(question, "Tạo câu hỏi thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật một câu hỏi",
            description = "Cập nhật nội dung và các tùy chọn câu trả lời cho một câu hỏi."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật câu hỏi thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy câu hỏi"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<QuestionDTO.Response> updateQuestion(
            @Parameter(description = "ID của câu hỏi") @PathVariable Long id,
            @Valid @RequestBody QuestionDTO.Request request) {
        log.info("Updating question with ID: {}", id);
        QuestionDTO.Response question = questionService.updateQuestion(id, request);
        return ApiResponse.success(question, "Cập nhật câu hỏi thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa một câu hỏi",
            description = "Xóa hoàn toàn một câu hỏi khỏi hệ thống."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa câu hỏi thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy câu hỏi")
    })
    public ApiResponse<Void> deleteQuestion(
            @Parameter(description = "ID của câu hỏi") @PathVariable Long id) {
        log.info("Deleting question with ID: {}", id);
        questionService.deleteQuestion(id);
        return ApiResponse.success(null, "Xóa câu hỏi thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin câu hỏi theo ID",
            description = "Lấy thông tin chi tiết của một câu hỏi cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin câu hỏi thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy câu hỏi")
    })
    public ApiResponse<QuestionDTO.Response> getQuestionById(
            @Parameter(description = "ID của câu hỏi") @PathVariable Long id) {
        log.info("Getting question by ID: {}", id);
        QuestionDTO.Response question = questionService.findById(id);
        return ApiResponse.success(question, "Lấy thông tin câu hỏi thành công");
    }

    @GetMapping("/search")
    @Operation(
            summary = "Tìm kiếm câu hỏi theo nội dung",
            description = "Tìm kiếm và phân trang các câu hỏi có chứa chuỗi tìm kiếm trong nội dung."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm kiếm câu hỏi thành công")
    })
    public ApiResponse<Page<QuestionDTO.Response>> searchQuestions(
            @Parameter(description = "Chuỗi tìm kiếm") @RequestParam(required = false) String textQuestion,
            @Parameter(hidden = true) Pageable pageable) {
        log.info("Searching questions for text: {}", textQuestion);
        Page<QuestionDTO.Response> questions = questionService.search(textQuestion, pageable);
        return ApiResponse.success(questions, "Tìm kiếm câu hỏi thành công");
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(
            summary = "Lấy danh sách câu hỏi theo Quiz",
            description = "Lấy tất cả các câu hỏi thuộc về một Quiz cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách câu hỏi thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy Quiz")
    })
    public ApiResponse<Page<QuestionDTO.Response>> getQuestionsByQuizId(
            @Parameter(description = "ID của Quiz") @PathVariable Long quizId,
            @Parameter(hidden = true) Pageable pageable) {
        log.info("Getting questions for quiz ID: {}", quizId);
        Page<QuestionDTO.Response> questions = questionService.findByQuizId(quizId, pageable);
        return ApiResponse.success(questions, "Lấy danh sách câu hỏi theo Quiz thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả câu hỏi",
            description = "Lấy danh sách tất cả các câu hỏi với phân trang."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách câu hỏi thành công")
    })
    public ApiResponse<Page<QuestionDTO.Response>> getAllQuestions(Pageable pageable) {
        log.info("Getting all questions");
        Page<QuestionDTO.Response> questions = questionService.findAll(pageable);
        return ApiResponse.success(questions, "Lấy danh sách tất cả câu hỏi thành công");
    }
}