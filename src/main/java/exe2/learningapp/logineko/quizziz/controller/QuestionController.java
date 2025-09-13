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
    public ApiResponse<QuestionDTO.Response> create(
            @Valid @RequestBody QuestionDTO.Request request) {
        log.info("Creating new question: {}", request.questionText());
        return ApiResponse.success(
                questionService.createQuestion(request),
                "Tạo câu hỏi thành công"
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật Question")
    public ApiResponse<QuestionDTO.Response> update(
            @Parameter(description = "ID Question") @PathVariable Long id,
            @Valid @RequestBody QuestionDTO.Request request) {
        log.info("Updating question {}", id);
        return ApiResponse.success(
                questionService.updateQuestion(id, request),
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
    public ApiResponse<QuestionDTO.Response> getById(
            @Parameter(description = "ID Question") @PathVariable Long id) {
        log.info("Fetching question {}", id);
        return ApiResponse.success(
                questionService.findById(id),
                "Lấy thành công"
        );
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả Question (có phân trang)")
    public ApiResponse<Page<QuestionDTO.Response>> getAll(
            @Parameter(description = "Thông tin phân trang (page, size, sort)") Pageable pageable) {
        log.info("Fetching all questions with pagination");
        return ApiResponse.success(
                questionService.findAll(pageable),
                "Lấy danh sách thành công"
        );
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm Question theo từ khóa")
    public ApiResponse<Page<QuestionDTO.Response>> search(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam String keyword,
            @Parameter(description = "Thông tin phân trang (page, size, sort)") Pageable pageable) {
        log.info("Searching questions with keyword: {}", keyword);
        return ApiResponse.success(
                questionService.search(keyword, pageable),
                "Tìm kiếm thành công"
        );
    }
}
