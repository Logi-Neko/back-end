package exe2.learningapp.logineko.quizziz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.time.Instant;
import java.util.List;

@Schema(name = "LeaderBoard")
public class LeaderBoardDTO{

    @Builder
    public record LeaderBoardResponse(
        Long participantId,
        String participantName,
        int score,
        Integer  rank
    ){}

    @Builder
    public record LeaderBoardUpdateEvent(
            String eventType, // "leaderboard.updated"
            Long contestId,
            List<LeaderBoardResponse> leaderboard,
            Instant timestamp
    ){}
}
