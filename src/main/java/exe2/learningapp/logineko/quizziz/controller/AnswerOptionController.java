package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.ApiResponse;
import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;
import exe2.learningapp.logineko.quizziz.service.AnswerOptionService;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/answer-options")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AnswerOption Management", description = "API quản lý các đáp án của Question")
public class AnswerOptionController {

    private final AnswerOptionService answerOptionService;

    @PostMapping("/{questionId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Thêm đáp án vào Question")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "201", description = "Thêm đáp án thành công"),
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ")
    })
    public ApiResponse<AnswerOptionDTO.Response> create(
            @Valid @RequestBody AnswerOptionDTO.Request request) {

        return ApiResponse.success(
                answerOptionService.create(request),
                "Thêm đáp án thành công"
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật đáp án")
    public ApiResponse<AnswerOptionDTO.Response> update(
            @Parameter(description = "ID AnswerOption") @PathVariable Long id,
            @Valid @RequestBody AnswerOptionDTO.Request request) {
        log.info("Updating answer option {}", id);
        return ApiResponse.success(
                answerOptionService.update(id, request),
                "Cập nhật thành công"
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Xóa đáp án")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID AnswerOption") @PathVariable Long id) {
        log.info("Deleting answer option {}", id);
        answerOptionService.delete(id);
        return ApiResponse.success(null, "Xóa thành công");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết đáp án theo ID")
    public ApiResponse<Optional<AnswerOptionDTO.Response>> getById(
            @Parameter(description = "ID AnswerOption") @PathVariable Long id) {
        log.info("Fetching answer option {}", id);
        return ApiResponse.success(
                answerOptionService.findById(id),
                "Lấy thành công"
        );
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Lấy danh sách đáp án theo Question (có phân trang)")
    public ApiResponse<List<AnswerOptionDTO.Response>> getByQuestion(
            @Parameter(description = "ID Question") @PathVariable Long questionId){
        log.info("Fetching answer options for question {}", questionId);
        return ApiResponse.success(
                answerOptionService.findByQuestion(questionId),
                "Lấy danh sách thành công"
        );
    }
}
