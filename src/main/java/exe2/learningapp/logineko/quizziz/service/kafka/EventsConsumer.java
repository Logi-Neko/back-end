package exe2.learningapp.logineko.quizziz.service.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
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

}
