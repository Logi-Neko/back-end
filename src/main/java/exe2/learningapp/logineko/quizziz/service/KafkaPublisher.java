package exe2.learningapp.logineko.quizziz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class KafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEvent(Long roomId, Object payload) {
        kafkaTemplate.send("room." + roomId + ".events", payload);
    }

    public void publishAnswer(Long roomId, Object payload) {
        kafkaTemplate.send("room." + roomId + ".answers", payload);
    }

}
