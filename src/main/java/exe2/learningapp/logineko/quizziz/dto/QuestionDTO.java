package exe2.learningapp.logineko.quizziz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
@Schema(name = "Question")
public class QuestionDTO {
    @Builder
    public record QuestionRequest(
            String questionText,
            List<AnswerOptionDTO.AnswerOptionRequest> answerOptions,
            Integer timeLimit,
            Integer points
    )
    {}
    @Builder
    public record QuestionResponse(
            Long id,
            String questionText,
            List<AnswerOptionDTO.AnswerOptionResponse> answerOptions,
            Integer points,
            Integer timeLimit
    ){}

}
