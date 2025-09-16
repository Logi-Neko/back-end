package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.common.dto.PaginatedResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Management", description = "API quản lý câu hỏi")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Tạo mới Question")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "201", description = "Tạo câu hỏi thành công"),
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ")
    })
    public ApiResponse<QuestionDTO.QuestionResponse> create(
            @Valid @RequestBody QuestionDTO.QuestionRequest questionRequest) {
        log.info("Creating new question: {}", questionRequest.questionText());
        return ApiResponse.success(
                questionService.createQuestion(questionRequest),
                "Tạo câu hỏi thành công"
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật Question")
    public ApiResponse<QuestionDTO.QuestionResponse> update(
            @Parameter(description = "ID Question") @PathVariable Long id,
            @Valid @RequestBody QuestionDTO.QuestionRequest questionRequest) {
        log.info("Updating question {}", id);
        return ApiResponse.success(
                questionService.updateQuestion(id, questionRequest),
                "Cập nhật thành công"
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Xóa Question")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID Question") @PathVariable Long id) {
        log.info("Deleting question {}", id);
        questionService.deleteQuestion(id);
        return ApiResponse.success(null, "Xóa thành công");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết Question theo ID")
    public ApiResponse<QuestionDTO.QuestionResponse> getById(
            @Parameter(description = "ID Question") @PathVariable Long id) {
        log.info("Fetching question {}", id);
        return ApiResponse.success(
                questionService.findById(id),
                "Lấy thành công"
        );
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả Question (có phân trang)")
    public ApiResponse<PaginatedResponse<QuestionDTO.QuestionResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Fetching all questions with pagination");

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<QuestionDTO.QuestionResponse> questions = questionService.findAll(pageable);

        PaginatedResponse<QuestionDTO.QuestionResponse> result = new PaginatedResponse<>(questions);

        return ApiResponse.success(result, "Lấy danh sách thành công");
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm Question theo từ khóa")
    public ApiResponse<PaginatedResponse<QuestionDTO.QuestionResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Searching questions with keyword: {}", keyword);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<QuestionDTO.QuestionResponse> questions = questionService.search(keyword, pageable);

        PaginatedResponse<QuestionDTO.QuestionResponse> result = new PaginatedResponse<>(questions);

        return ApiResponse.success(result, "Tìm kiếm thành công");
    }
}
