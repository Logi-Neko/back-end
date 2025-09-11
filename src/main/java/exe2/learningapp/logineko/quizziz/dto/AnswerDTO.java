package exe2.learningapp.logineko.quizziz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class AnswerDTO {

    @Builder
    public record CreateRequest(
            @NotNull(message = "Participant ID must not be null")
            Long participantId,
            @NotNull(message = "Question ID must not be null")
            Long questionId,
            @NotNull(message = "Selected option ID must not be null")
            Long selectedOptionId,
            @NotNull(message = "Answer time must not be null")
            @Min(value = 0, message = "Answer time must be a non-negative value")
            Integer answerTime
    ) {}

    @Builder
    public record Response(
            Long id,
            String answerText,
            Boolean isCorrect,
            Integer answerTime,
            Integer score,
            Long participantId,
            Long questionId,
            Long selectedOptionId
    ) {}

    @Builder
    public record ScoreResponse(
            Long questionId,
            Long selectedOptionId,
            boolean isCorrect,
            int score
    ) {}
}
