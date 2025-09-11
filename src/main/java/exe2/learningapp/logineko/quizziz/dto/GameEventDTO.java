package exe2.learningapp.logineko.quizziz.dto;

import lombok.Builder;

@Builder
public record GameEventDTO
        (
                String type,        // QUESTION_START, ANSWER_SUBMITTED, LEADERBOARD_UPDATE
                Long quizId,
                Long questionId,
                Long participantId,
                Object payload
        )
{
}
