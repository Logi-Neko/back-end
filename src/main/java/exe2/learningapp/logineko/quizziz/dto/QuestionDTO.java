package exe2.learningapp.logineko.quizziz.dto;

import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import lombok.Builder;

import java.util.List;

public class QuestionDTO {
    @Builder
    public record Request(
            Long quizId,
            String questionText,
            List<AnswerOption> answerOptions
    )
    {}
    @Builder
    public record Response(
            Long id,
            String questionText,
            List<AnswerOption> answerOptions,
            Integer points,
            Long quizId
    ){}

}
