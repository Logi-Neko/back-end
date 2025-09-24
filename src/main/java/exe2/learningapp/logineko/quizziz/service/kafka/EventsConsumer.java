package exe2.learningapp.logineko.quizziz.service.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
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
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // Legacy room events (keeping for backward compatibility)
    @KafkaListener(topicPattern = "room\\..*\\.events", containerFactory = "kafkaListenerContainerFactory")
    public void consumeEvent(Object rec) {
        Map<String,Object> payload = objectMapper.convertValue(rec, new TypeReference<>(){});
        String eventType = String.valueOf(payload.get("eventType"));
        Long roomId = Long.valueOf(String.valueOf(payload.get("roomId")));
        Object eventPayload = payload.get("payload");

        switch (eventType){
            case "START_QUIZ":
            case "NEXT_QUESTION":
            case "ANSWER_SUBMITTED":
            case "END_QUESTION":
            case "END_QUIZ":
                simpMessagingTemplate.convertAndSend("/topic/room/" + roomId , eventPayload);
                break;
            default:
                log.warn("Unknown legacy event type: {}", eventType);
                break;
        }
    }

    // Question Events
    @KafkaListener(topics = "question.revealed", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeQuestionRevealed(GameEventDTO.QuestionRevealedEvent event) {
        log.info("Received question revealed event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
        
        // Also send to specific question topic
        String questionDestination = "/topic/contest." + event.contestId() + ".question." + event.contestQuestionId();
        simpMessagingTemplate.convertAndSend(questionDestination, event);
    }

    @KafkaListener(topics = "question.ended", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeQuestionEnded(GameEventDTO.QuestionEndedEvent event) {
        log.info("Received question ended event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
    }

    // Answer Events
    @KafkaListener(topics = "answer.submitted", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeAnswerSubmitted(GameEventDTO.AnswerSubmittedEvent event) {
        log.info("Received answer submitted event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
        
        // Also send to participant-specific topic
        String participantDestination = "/topic/contest." + event.contestId() + ".participant." + event.participantId();
        simpMessagingTemplate.convertAndSend(participantDestination, event);
    }

    // Score Events
    @KafkaListener(topics = "score.updated", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeScoreUpdated(GameEventDTO.ScoreUpdatedEvent event) {
        log.info("Received score updated event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
        
        // Also send to participant-specific topic
        String participantDestination = "/topic/contest." + event.contestId() + ".participant." + event.participantId();
        simpMessagingTemplate.convertAndSend(participantDestination, event);
    }

    // Contest Lifecycle Events
    @KafkaListener(topics = "contest.lifecycle", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeContestLifecycle(GameEventDTO.ContestLifecycleEvent event) {
        log.info("Received contest lifecycle event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
        
        // Send to global contest topic for monitoring
        simpMessagingTemplate.convertAndSend("/topic/contests", event);
    }

    // Leaderboard Events
    @KafkaListener(topics = "leaderboard.updated", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeLeaderboardUpdated(GameEventDTO.LeaderboardUpdatedEvent event) {
        log.info("Received leaderboard updated event: {}", event);
        String destination = "/topic/contest." + event.contestId() + ".leaderboard";
        simpMessagingTemplate.convertAndSend(destination, event);
        
        // Also send to main contest topic
        String mainDestination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(mainDestination, event);
    }

    // Game State Events
    @KafkaListener(topics = "game.state.changed", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeGameStateChanged(GameEventDTO.GameStateChangedEvent event) {
        log.info("Received game state changed event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
    }

    // Notification Events
    @KafkaListener(topics = "notification", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeNotification(GameEventDTO.NotificationEvent event) {
        log.info("Received notification event: {}", event);
        
        if (event.participantId() != null) {
            // Send to specific participant
            String destination = "/topic/contest." + event.contestId() + ".participant." + event.participantId();
            simpMessagingTemplate.convertAndSend(destination, event);
        } else {
            // Broadcast to all participants in contest
            String destination = "/topic/contest." + event.contestId();
            simpMessagingTemplate.convertAndSend(destination, event);
        }
    }

    // Time Warning Events
    @KafkaListener(topics = "time.warning", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTimeWarning(GameEventDTO.TimeWarningEvent event) {
        log.info("Received time warning event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
    }

    // Contest Results Events
    @KafkaListener(topics = "contest.results", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeContestResults(GameEventDTO.ContestResultsEvent event) {
        log.info("Received contest results event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
        
        // Also send to results-specific topic
        String resultsDestination = "/topic/contest." + event.contestId() + ".results";
        simpMessagingTemplate.convertAndSend(resultsDestination, event);
    }
}
