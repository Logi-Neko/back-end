package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushService {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "question.revealed", groupId = "push-service")
    public void onQuestionRevealed(GameEventDTO.QuestionRevealedEvent ev) {
        String dest = "/topic/contest." + ev.contestId();
        log.info("Sending question revealed event to: {}, event: {}", dest, ev);
        messagingTemplate.convertAndSend(dest, ev);
    }

    @KafkaListener(topics = "score.updated", groupId = "push-service")
    public void onScoreUpdated(GameEventDTO.ScoreUpdatedEvent ev) {
        String dest = "/topic/contest." + ev.contestId();
        log.info("Sending score updated event to: {}, event: {}", dest, ev);
        messagingTemplate.convertAndSend(dest, ev);
    }

    @KafkaListener(topics = "contest.lifecycle", groupId = "push-service")
    public void onLifecycle(GameEventDTO.ContestLifecycleEvent ev) {
        String dest = "/topic/contest." + ev.contestId();
        log.info("Sending lifecycle event to: {}, event: {}", dest, ev);
        messagingTemplate.convertAndSend(dest, ev);
    }

    @KafkaListener(topics = "leaderboard.updated", groupId = "push-service")
    public void onLeaderboardUpdated(LeaderBoardDTO.LeaderBoardUpdateEvent ev) {
        String dest = "/topic/contest." + ev.contestId();
        log.info("Sending leaderboard update to: {}, event: {}", dest, ev);
        messagingTemplate.convertAndSend(dest, ev);
    }
}