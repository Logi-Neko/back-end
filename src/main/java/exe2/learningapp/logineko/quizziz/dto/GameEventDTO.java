package exe2.learningapp.logineko.quizziz.dto;

import lombok.Builder;

import java.time.Instant;

public class GameEventDTO
{
        @Builder
        public record QuestionRevealedEvent(
                String eventType, // "question.revealed"
                Long contestId,
                Long contestQuestionId,
                Integer orderIndex,
                QuestionDTO.QuestionResponse question,
                Instant timestamp
        ) {
              //  public record QuestionPayload(Long questionId, String content, List<Option> options, Integer timeLimitSeconds) {}
                public record Option(String optionId, String text) {}
        }
       @Builder
        public record AnswerSubmittedEvent(
                String eventType, // "answer.submitted"
                Long contestId,
                Long contestQuestionId,
                Long participantId,
                Long answerOptionId,
                Instant answeredAt,
                Long timeTakenMs,
                Long submissionUuid // client-generated or server-generated unique id for idempotency
        ) {}
        @Builder
        public record ScoreUpdatedEvent(
                String eventType, // "score.updated"
                Long contestId,
                Long participantId,
                Integer newScore,
                Integer rank,
                Instant timestamp
        ) {}
        @Builder
        public record ContestLifecycleEvent(
                String eventType, // "contest.started", "contest.ended", "participant.joined", "participant.left"
                Long contestId,
                Long participantId, // optional
                Instant timestamp
        ) {}
}
