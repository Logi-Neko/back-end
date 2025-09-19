package exe2.learningapp.logineko.quizziz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
@Schema(name = "ContestQuestion")
public class ContestQuestionDTO {
    @Builder
    public record ContestQuestionRequest(
            Long contestId,
            Long questionId,
            Integer index

    ){}
    @Builder
    public record ContestQuestionResponse(
            Long id,
            Long contestId,
            Long questionId,
            Integer index
    ){}
}