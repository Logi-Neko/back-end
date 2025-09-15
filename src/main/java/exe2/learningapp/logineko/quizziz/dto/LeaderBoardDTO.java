package exe2.learningapp.logineko.quizziz.dto;

import lombok.Builder;

public class LeaderBoardDTO{

    @Builder
    public record Response(
        Long participantId,
        int score,
        Integer  rank
    ){}
}
