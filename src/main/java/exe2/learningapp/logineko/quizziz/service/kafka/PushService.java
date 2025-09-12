package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushService {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "question.revealed", groupId = "push-service")
    public void onQuestionRevealed(GameEventDTO.QuestionRevealedEvent ev) {
        String dest = "/topic/contest." + ev.contestId();
        messagingTemplate.convertAndSend(dest, ev);
    }

    @KafkaListener(topics = "score.updated", groupId = "push-service")
    public void onScoreUpdated(GameEventDTO.ScoreUpdatedEvent ev) {
        String dest = "/topic/contest." + ev.contestId();
        messagingTemplate.convertAndSend(dest, ev);
    }

    @KafkaListener(topics = "contest.lifecycle", groupId = "push-service")
    public void onLifecycle(GameEventDTO.ContestLifecycleEvent ev) {
        String dest = "/topic/contest." + ev.contestId();
        messagingTemplate.convertAndSend(dest, ev);
    }

}
