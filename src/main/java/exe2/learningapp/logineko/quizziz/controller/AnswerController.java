package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.common.ApiResponse;
import exe2.learningapp.logineko.quizziz.dto.AnswerDTO;
import exe2.learningapp.logineko.quizziz.service.AnswerService;
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

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Answer Management", description = "API quản lý câu trả lời của người tham gia")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Gửi một câu trả lời mới",
            description = "Ghi nhận câu trả lời của người tham gia cho một câu hỏi."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Gửi câu trả lời thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người tham gia, câu hỏi hoặc tùy chọn câu trả lời"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ApiResponse<AnswerDTO.Response> submitAnswer(
            @Valid @RequestBody AnswerDTO.CreateRequest request) {
        log.info("Submitting answer for participant ID: {}, question ID: {} with selected option ID: {}",
                request.participantId(), request.questionId(), request.selectedOptionId());
        AnswerDTO.Response response = answerService.submitAnswer(request);
        return ApiResponse.success(response, "Câu trả lời đã được ghi nhận thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin câu trả lời theo ID",
            description = "Lấy thông tin chi tiết của một câu trả lời cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy câu trả lời")
    })
    public ApiResponse<AnswerDTO.Response> getAnswerById(
            @Parameter(description = "ID của câu trả lời") @PathVariable Long id) {
        log.info("Getting answer with ID: {}", id);
        AnswerDTO.Response response = answerService.findById(id);
        return ApiResponse.success(response, "Lấy thông tin câu trả lời thành công");
    }

    @GetMapping("/question/{questionId}")
    @Operation(
            summary = "Lấy danh sách câu trả lời theo câu hỏi",
            description = "Lấy danh sách tất cả các câu trả lời đã được gửi cho một câu hỏi cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy câu hỏi")
    })
    public ApiResponse<List<AnswerDTO.Response>> getAnswersByQuestionId(
            @Parameter(description = "ID của câu hỏi") @PathVariable Long questionId) {
        log.info("Getting answers for question ID: {}", questionId);
        List<AnswerDTO.Response> responses = answerService.findAllByQuestionId(questionId);
        return ApiResponse.success(responses, "Lấy danh sách câu trả lời thành công");
    }

    @GetMapping("/participant/{participantId}")
    @Operation(
            summary = "Lấy danh sách câu trả lời theo người tham gia",
            description = "Lấy danh sách tất cả các câu trả lời của một người tham gia cụ thể."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người tham gia")
    })
    public ApiResponse<Page<AnswerDTO.Response>> getAnswersByParticipantId(
            @Parameter(description = "ID của người tham gia") @PathVariable Long participantId,
            @Parameter(hidden = true) Pageable pageable) {
        log.info("Getting answers for participant ID: {}", participantId);
        Page<AnswerDTO.Response> responses = answerService.findAllByParticipantId(participantId, pageable);
        return ApiResponse.success(responses, "Lấy danh sách câu trả lời thành công");
    }
}
