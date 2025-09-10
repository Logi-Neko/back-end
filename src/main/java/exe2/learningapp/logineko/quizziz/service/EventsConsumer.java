package exe2.learningapp.logineko.quizziz.service;

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
        Long roomId = Long.valueOf(String.valueOf(payload.get("roomId")));
        simpMessagingTemplate.convertAndSend("/topic/room." + roomId, payload);
    }

}
