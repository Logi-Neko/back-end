package exe2.learningapp.logineko.quizziz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
@Schema(name = "Answer")
public class AnswerDTO {

    @Builder
    public record AnswerRequest(
            @NotNull(message = "Participant ID must not be null")
            Long participantId,
            @NotNull(message = "Question ID must not be null")
            Long contestQuestionId,
            @NotNull(message = "Selected option ID must not be null")
            Long answerOptionId,
            @NotNull(message = "Answer time must not be null")
            @Min(value = 0, message = "Answer time must be a non-negative value")
            Integer answerTime
    ) {}

    @Builder
    public record AnswerResponse(
            Long id,
            Boolean isCorrect,
            Integer answerTime,
            Integer score,
            Long participantId,
            Long questionId,
            Long answerOptionId
    ) {}

    @Builder
    public record ScoreResponse(
            Long questionId,
            Long selectedOptionId,
            boolean isCorrect,
            int score
    ) {}
}
