package exe2.learningapp.logineko.quizziz.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

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
        public record QuestionEndedEvent(
                String eventType, // "question.ended"
                Long contestId,
                Long contestQuestionId,
                Integer orderIndex,
                Instant timestamp
        ) {}
        
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
                String eventType, // "contest.created", "contest.started", "contest.ended", "participant.joined", "participant.left"
                Long contestId,
                Long participantId, // optional
                Instant timestamp
        ) {}
        
        @Builder
        public record LeaderboardUpdatedEvent(
                String eventType, // "leaderboard.updated"
                Long contestId,
                List<LeaderBoardDTO.LeaderBoardResponse> leaderboard,
                Instant timestamp
        ) {}
        
        @Builder
        public record GameStateChangedEvent(
                String eventType, // "game.state.changed"
                Long contestId,
                String state, // "waiting", "question_active", "question_ended", "showing_results", "finished"
                Integer currentQuestionIndex,
                Integer totalQuestions,
                Instant timestamp
        ) {}
        
        @Builder
        public record NotificationEvent(
                String eventType, // "notification"
                Long contestId,
                Long participantId, // optional, null for broadcast
                String message,
                String type, // "info", "warning", "error", "success"
                Instant timestamp
        ) {}
        
        @Builder
        public record TimeWarningEvent(
                String eventType, // "time.warning"
                Long contestId,
                Long contestQuestionId,
                Integer remainingSeconds,
                Instant timestamp
        ) {}
        
        @Builder
        public record ContestResultsEvent(
                String eventType, // "contest.results"
                Long contestId,
                List<LeaderBoardDTO.LeaderBoardResponse> finalLeaderboard,
                Instant timestamp
        ) {}
}
