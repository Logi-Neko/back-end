package exe2.learningapp.logineko.quizziz.service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exe2.learningapp.logineko.quizziz.service.kafka.processor.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameProcessor gameProcessor;
    private final LeaderboardProcessor leaderboardProcessor;
    private final ParticipantProcessor participantProcessor;
    private final ContestStateManager contestStateManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "game-events",
            groupId = "events-consumer",
            containerFactory = "kafkaListenerContainerFactory" // giữ nguyên StringDeserializer
    )
    public void consumeGameEvent(Map<String, Object> message) {
        log.info("📥 Received raw game event: {}", message);

        try {
          //  JsonNode json = objectMapper.readTree(message);
            JsonNode json = objectMapper.convertValue(message, JsonNode.class);
            String eventType = (String) message.get("eventType");
            Long contestId = Long.valueOf(message.get("contestId").toString());

            if (contestId == null) {
                log.warn("⚠️ Missing contestId in event: {}", message);
                return;
            }

            switch (eventType) {
                case "contest.created":
                case "contest.started":
                case "contest.ended":
                    handleContestLifecycleEvent(json, eventType, contestId);
                    break;
                case "question.revealed":
                    handleQuestionRevealedEvent(json, contestId);
                    break;
                case "answer.submitted":
                    handleAnswerSubmittedEvent(json, contestId);
                    break;
                case "score.updated":
                    handleScoreUpdatedEvent(json, contestId);
                    break;
                case "leaderboard.updated":
                    handleLeaderboardUpdatedEvent(json, contestId);
                    break;
                case "leaderboard.refresh":
                    handleLeaderboardRefreshEvent(json, contestId);
                    break;
                case "participant.created":
                    handleParticipantCreatedEvent(json, contestId);
                    break;
                default:
                    log.warn("⚠️ Unknown event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("❌ Error processing game event: {}", e.getMessage(), e);
        }
    }

    private void handleContestLifecycleEvent(JsonNode event, String eventType, Long contestId) {
        log.info("➡️ Processing contest lifecycle event: {} for contest {}", eventType, contestId);
        contestStateManager.handleContestLifecycleEvent(event, eventType, contestId);
        messagingTemplate.convertAndSend("/topic/contest." + contestId, event);
        messagingTemplate.convertAndSend("/topic/contests", event);
    }

    private void handleQuestionRevealedEvent(JsonNode event, Long contestId) {
        log.info("➡️ Processing question revealed event for contest {}", contestId);
        gameProcessor.handleQuestionRevealed(event, contestId);
        messagingTemplate.convertAndSend("/topic/contest." + contestId, event);

        if (event.has("contestQuestionId")) {
            Long contestQuestionId = event.get("contestQuestionId").asLong();
            messagingTemplate.convertAndSend("/topic/contest." + contestId + ".question." + contestQuestionId, event);
        }
    }

    private void handleAnswerSubmittedEvent(JsonNode event, Long contestId) {
        log.info("➡️ Processing answer submitted event for contest {}", contestId);
        gameProcessor.handleAnswerSubmitted(event, contestId);
        messagingTemplate.convertAndSend("/topic/contest." + contestId, event);

        if (event.has("participantId")) {
            Long participantId = event.get("participantId").asLong();
            messagingTemplate.convertAndSend("/topic/contest." + contestId + ".participant." + participantId, event);
        }
    }

    private void handleScoreUpdatedEvent(JsonNode event, Long contestId) {
        log.info("➡️ Processing score updated event for contest {}", contestId);
        leaderboardProcessor.handleScoreUpdated(event, contestId);
        messagingTemplate.convertAndSend("/topic/contest." + contestId, event);

        if (event.has("participantId")) {
            Long participantId = event.get("participantId").asLong();
            messagingTemplate.convertAndSend("/topic/contest." + contestId + ".participant." + participantId, event);
        }
    }

    private void handleLeaderboardUpdatedEvent(JsonNode event, Long contestId) {
        log.info("➡️ Processing leaderboard updated event for contest {}", contestId);
        messagingTemplate.convertAndSend("/topic/contest." + contestId + ".leaderboard", event);
        messagingTemplate.convertAndSend("/topic/contest." + contestId, event);
    }

    private void handleLeaderboardRefreshEvent(JsonNode event, Long contestId) {
        log.info("➡️ Processing leaderboard refresh event for contest {}", contestId);
        leaderboardProcessor.handleLeaderboardRefresh(event, contestId);
    }

    private void handleParticipantCreatedEvent(JsonNode event, Long contestId) {
        log.info("➡️ Processing participant created event for contest {}", contestId);
        participantProcessor.handleParticipantCreated(event, contestId);
        messagingTemplate.convertAndSend("/topic/contest." + contestId, event);

        if (event.has("participantId")) {
            Long participantId = event.get("participantId").asLong();
            messagingTemplate.convertAndSend("/topic/contest." + contestId + ".participant." + participantId, event);
        }
    }
}
