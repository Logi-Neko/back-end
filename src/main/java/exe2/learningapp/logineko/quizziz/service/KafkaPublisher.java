package exe2.learningapp.logineko.quizziz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEvent(Long roomId,String eventType, Object payload) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType" ,eventType);
        event.put("roomId" ,roomId);
        event.put("payload" ,payload);
        kafkaTemplate.send("room." + roomId + ".events", payload);
    }



}
