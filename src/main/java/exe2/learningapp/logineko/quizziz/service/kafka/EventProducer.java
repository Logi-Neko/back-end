package exe2.learningapp.logineko.quizziz.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishQuestionRevealed(Long contestId, Object event) {
        kafkaTemplate.send("question.revealed", String.valueOf(contestId), event);
    }

    public void publishAnswerSubmitted(Long contestId, Object event) {
        kafkaTemplate.send("answer.submitted", String.valueOf(contestId), event);
    }

    public void publishScoreUpdated(Long contestId, Object event) {
        kafkaTemplate.send("score.updated", String.valueOf(contestId), event);
    }

    public void publishContestLifecycle(Long contestId, Object event) {
        kafkaTemplate.send("contest.lifecycle", String.valueOf(contestId), event);
    }
}
