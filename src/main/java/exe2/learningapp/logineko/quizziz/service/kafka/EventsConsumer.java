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
                // Unknown event type, do nothing or log a warning
                break;
        }
    }

    @KafkaListener(topics = "answer.submitted", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeAnswerSubmitted(GameEventDTO.AnswerSubmittedEvent event) {
        log.info("Received answer submitted event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
    }

    @KafkaListener(topics = "question.revealed", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeQuestionRevealed(GameEventDTO.QuestionRevealedEvent event) {
        log.info("Received question revealed event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
    }

    @KafkaListener(topics = "score.updated", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeScoreUpdated(GameEventDTO.ScoreUpdatedEvent event) {
        log.info("Received score updated event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
    }

    @KafkaListener(topics = "contest.lifecycle", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeContestLifecycle(GameEventDTO.ContestLifecycleEvent event) {
        log.info("Received contest lifecycle event: {}", event);
        String destination = "/topic/contest." + event.contestId();
        simpMessagingTemplate.convertAndSend(destination, event);
    }
}
