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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answer-options")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Answer Option Management", description = "API quản lý các tùy chọn câu trả lời")
public class AnswerOptionController {

    private final AnswerOptionService answerOptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo một tùy chọn câu trả lời mới",
            description = "Tạo một tùy chọn câu trả lời và liên kết nó với một câu hỏi."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo tùy chọn câu trả lời thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy câu hỏi"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ")
    })
    public ApiResponse<AnswerOptionDTO.Response> createAnswerOption(
            @Valid @RequestBody AnswerOptionDTO.Request request) {
        log.info("Creating a new answer option for question ID: {}", request.questionId());
        AnswerOptionDTO.Response response = answerOptionService.create(request);
        return ApiResponse.success(response, "Tạo tùy chọn câu trả lời thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật một tùy chọn câu trả lời",
            description = "Cập nhật nội dung và trạng thái đúng/sai cho một tùy chọn câu trả lời."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật tùy chọn câu trả lời thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tùy chọn câu trả lời"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<AnswerOptionDTO.Response> updateAnswerOption(
            @Parameter(description = "ID của tùy chọn câu trả lời") @PathVariable Long id,
            @Valid @RequestBody AnswerOptionDTO.Request request) {
        log.info("Updating answer option with ID: {}", id);
        AnswerOptionDTO.Response response = answerOptionService.update(id, request);
        return ApiResponse.success(response, "Cập nhật tùy chọn câu trả lời thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa một tùy chọn câu trả lời",
            description = "Xóa hoàn toàn một tùy chọn câu trả lời khỏi hệ thống."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa tùy chọn câu trả lời thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tùy chọn câu trả lời")
    })
    public ApiResponse<Void> deleteAnswerOption(
            @Parameter(description = "ID của tùy chọn câu trả lời") @PathVariable Long id) {
        log.info("Deleting answer option with ID: {}", id);
        answerOptionService.delete(id);
        return ApiResponse.success(null, "Xóa tùy chọn câu trả lời thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin tùy chọn câu trả lời theo ID",
            description = "Lấy thông tin chi tiết của một tùy chọn câu trả lời cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tùy chọn câu trả lời")
    })
    public ApiResponse<AnswerOptionDTO.Response> getAnswerOptionById(
            @Parameter(description = "ID của tùy chọn câu trả lời") @PathVariable Long id) {
        log.info("Getting answer option by ID: {}", id);
        AnswerOptionDTO.Response response = answerOptionService.findById(id);
        return ApiResponse.success(response, "Lấy thông tin tùy chọn câu trả lời thành công");
    }

    @GetMapping("/question/{questionId}")
    @Operation(
            summary = "Lấy danh sách tùy chọn câu trả lời theo câu hỏi",
            description = "Lấy tất cả các tùy chọn câu trả lời thuộc về một câu hỏi cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy câu hỏi")
    })
    public ApiResponse<List<AnswerOptionDTO.Response>> getAnswerOptionsByQuestionId(
            @Parameter(description = "ID của câu hỏi") @PathVariable Long questionId) {
        log.info("Getting answer options for question ID: {}", questionId);
        List<AnswerOptionDTO.Response> responses = answerOptionService.findAllByQuestionId(questionId);
        return ApiResponse.success(responses, "Lấy danh sách tùy chọn câu trả lời thành công");
    }
}