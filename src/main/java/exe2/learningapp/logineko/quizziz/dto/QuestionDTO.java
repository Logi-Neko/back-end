package exe2.learningapp.logineko.quizziz.dto;

import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import lombok.Builder;

import java.util.List;

public class QuestionDTO {
    @Builder
    public record Request(
            String questionText,
            List<AnswerOptionDTO.Request> answerOptions,
            Integer timeLimit,
            Integer points
    )
    {}
    @Builder
    public record Response(
            Long id,
            String questionText,
            List<AnswerOptionDTO.Response> answerOptions,
            Integer points,
            Integer timeLimit
    ){}

}
