package exe2.learningapp.logineko.quizziz.dto;


import lombok.*;
import java.time.Instant;

public class GameEventDTO {

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ContestLifecycleEvent {
                private String eventType; // contest.created, contest.started, contest.ended
                private Long contestId;
                private Instant timestamp;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class QuestionRevealedEvent {
                private String eventType; // question.revealed
                private Long contestId;
                private Long contestQuestionId;
                private Integer orderIndex;
                private Object question;
                private Instant timestamp;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class AnswerSubmittedEvent {
                private String eventType; // answer.submitted
                private Long contestId;
                private Long participantId;
                private Long contestQuestionId;
                private String answer;
                private Instant timestamp;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ScoreUpdatedEvent {
                private String eventType; // score.updated
                private Long contestId;
                private Long participantId;
                private Integer score;
                private Instant timestamp;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class LeaderboardUpdatedEvent {
                private String eventType; // leaderboard.updated
                private Long contestId;
                private Object leaderboard;
                private Instant timestamp;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class LeaderboardRefreshEvent {
                private String eventType; // leaderboard.refresh
                private Long contestId;
                private Instant timestamp;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ParticipantCreatedEvent {
                private String eventType; // participant.created
                private Long contestId;
                private Long participantId;
                private String name;
                private Instant timestamp;
        }
}
