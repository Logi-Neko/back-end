package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.quizziz.dto.ContestQuestionDTO;
import exe2.learningapp.logineko.quizziz.service.ContestQuestionService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contest-questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ContestQuestion Management", description = "API quản lý câu hỏi trong Contest")
public class ContestQuestionController {

    private final ContestQuestionService contestQuestionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Thêm câu hỏi vào Contest")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "201", description = "Thêm câu hỏi thành công"),
            @io.swagger.v3.oas.annotations.responses.
                    ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ")
    })
    public ApiResponse<ContestQuestionDTO.ContestQuestionResponse> addQuestion(
            @Valid @RequestBody ContestQuestionDTO.ContestQuestionRequest contestQuestionRequest) {
        log.info("Adding question {} to contest {}", contestQuestionRequest.questionId(), contestQuestionRequest.contestId());
        return ApiResponse.success(
                contestQuestionService.addQuestionToContest(contestQuestionRequest),
                "Thêm câu hỏi vào contest thành công"
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật câu hỏi trong Contest")
    public ApiResponse<ContestQuestionDTO.ContestQuestionResponse> update(
            @Parameter(description = "ID ContestQuestion") @PathVariable Long id,
            @Valid @RequestBody ContestQuestionDTO.ContestQuestionRequest contestQuestionRequest) {
        log.info("Updating ContestQuestion {}", id);
        return ApiResponse.success(
                contestQuestionService.update(id, contestQuestionRequest),
                "Cập nhật thành công"
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Xóa câu hỏi khỏi Contest")
    public ApiResponse<Void> delete(
            @Parameter(description = "ID ContestQuestion") @PathVariable Long id) {
        log.info("Deleting ContestQuestion {}", id);
        contestQuestionService.delete(id);
        return ApiResponse.success(null, "Xóa thành công");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết ContestQuestion theo ID")
    public ApiResponse<ContestQuestionDTO.ContestQuestionResponse> getById(
            @Parameter(description = "ID ContestQuestion") @PathVariable Long id) {
        log.info("Fetching ContestQuestion {}", id);
        return ApiResponse.success(
                contestQuestionService.findById(id)
                        .orElseThrow(() -> new RuntimeException("ContestQuestion not found")),
                "Lấy thành công"
        );
    }

    @GetMapping("/contest/{contestId}")
    @Operation(summary = "Lấy danh sách câu hỏi theo Contest (có phân trang)")
    public ApiResponse<Page<ContestQuestionDTO.ContestQuestionResponse>> getByContest(
            @Parameter(description = "ID Contest") @PathVariable Long contestId,
            @Parameter(description = "Thông tin phân trang (page, size, sort)") Pageable pageable) {
        log.info("Fetching ContestQuestions for Contest {} with pagination", contestId);
        return ApiResponse.success(
                contestQuestionService.findByContest(contestId, pageable),
                "Lấy danh sách thành công"
        );
    }
}