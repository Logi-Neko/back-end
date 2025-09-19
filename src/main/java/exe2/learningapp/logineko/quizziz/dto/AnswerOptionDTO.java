package exe2.learningapp.logineko.quizziz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
@Schema(name = "AnswerOption")
public class AnswerOptionDTO {

    @Builder
    public record AnswerOptionRequest(
            @NotNull(message = "Question ID must not be null")
            Long questionId,
            @NotBlank(message = "Option text must not be blank")
            String optionText,
            @NotNull(message = "isCorrect must not be null")
            Boolean isCorrect
    ) {}

    @Builder
    public record AnswerOptionResponse(
            Long id,
            String optionText,
            Boolean isCorrect,
            Long questionId
    ) {}
}