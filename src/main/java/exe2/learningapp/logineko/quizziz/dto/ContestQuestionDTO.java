package exe2.learningapp.logineko.quizziz.dto;

import lombok.Builder;

public class ContestQuestionDTO {
    @Builder
    public record Request(
            Long contestId,
            Long questionId,
            Integer index

    ){}
    @Builder
    public record Response(
            Long id,
            Long contestId,
            Long questionId,
            Integer index
    ){}
}