package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Question Events
    public void publishQuestionRevealed(Long contestId, GameEventDTO.QuestionRevealedEvent event) {
        log.info("Publishing question revealed event for contest {}: {}", contestId, event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("question.revealed", String.valueOf(contestId), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Question revealed event sent successfully for contest {}: {}", contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send question revealed event for contest {}: {}", contestId, ex.getMessage());
            }
        });
    }

    // Answer Events
    public void publishAnswerSubmitted(Long contestId, GameEventDTO.AnswerSubmittedEvent event) {
        log.info("Publishing answer submitted event for contest {}: {}", contestId, event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("answer.submitted", String.valueOf(contestId), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Answer submitted event sent successfully for contest {}: {}", contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send answer submitted event for contest {}: {}", contestId, ex.getMessage());
            }
        });
    }

    // Score Events
    public void publishScoreUpdated(Long contestId, GameEventDTO.ScoreUpdatedEvent event) {
        log.info("Publishing score updated event for contest {}: {}", contestId, event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("score.updated", String.valueOf(contestId), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Score updated event sent successfully for contest {}: {}", contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send score updated event for contest {}: {}", contestId, ex.getMessage());
            }
        });
    }

    // Contest Lifecycle Events
    public void publishContestLifecycle(Long contestId, GameEventDTO.ContestLifecycleEvent event) {
        log.info("Publishing contest lifecycle event for contest {}: {}", contestId, event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("contest.lifecycle", String.valueOf(contestId), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Contest lifecycle event sent successfully for contest {}: {}", contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send contest lifecycle event for contest {}: {}", contestId, ex.getMessage());
            }
        });
    }

    // Leaderboard Events
    public void publishLeaderboardUpdated(Long contestId, Object event) {
        log.info("Publishing leaderboard updated event for contest {}: {}", contestId, event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("leaderboard.updated", String.valueOf(contestId), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Leaderboard updated event sent successfully for contest {}: {}", contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send leaderboard updated event for contest {}: {}", contestId, ex.getMessage());
            }
        });
    }

    // Game State Events
    public void publishGameStateChanged(Long contestId, Object event) {
        log.info("Publishing game state changed event for contest {}: {}", contestId, event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("game.state.changed", String.valueOf(contestId), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Game state changed event sent successfully for contest {}: {}", contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send game state changed event for contest {}: {}", contestId, ex.getMessage());
            }
        });
    }

    // Notification Events
    public void publishNotification(Long contestId, Object event) {
        log.info("Publishing notification event for contest {}: {}", contestId, event);
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("notification", String.valueOf(contestId), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Notification event sent successfully for contest {}: {}", contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send notification event for contest {}: {}", contestId, ex.getMessage());
            }
        });
    }
}
