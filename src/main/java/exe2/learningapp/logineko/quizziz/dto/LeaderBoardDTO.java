package exe2.learningapp.logineko.quizziz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
@Schema(name = "LeaderBoard")
public class LeaderBoardDTO{

    @Builder
    public record LeaderBoardResponse(
        Long participantId,
        int score,
        Integer  rank
    ){}
}
