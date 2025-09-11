package exe2.learningapp.logineko.quizziz.dto;

public record LeaderBoardEntry(
        Long participantId,
        String nickName,
        int score,
        int totalScore

) {
}
