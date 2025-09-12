package exe2.learningapp.logineko.quizziz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
public class AnswerOptionDTO {

    @Builder
    public record Request(
            @NotNull(message = "Question ID must not be null")
            Long questionId,
            @NotBlank(message = "Option text must not be blank")
            String optionText,
            @NotNull(message = "isCorrect must not be null")
            Boolean isCorrect
    ) {}

    @Builder
    public record Response(
            Long id,
            String optionText,
            Boolean isCorrect,
            Long questionId
    ) {}
}