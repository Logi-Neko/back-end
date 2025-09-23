package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import exe2.learningapp.logineko.quizziz.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardProcessor {
    private final LeaderBoardService leaderBoardService;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "score.updated", groupId = "leaderboard-processor", containerFactory = "kafkaListenerContainerFactory")
    public void onScoreUpdated(GameEventDTO.ScoreUpdatedEvent event) {
        log.info("Processing score update for leaderboard: {}", event);
        
        try {
            // Update leaderboard in database
            leaderBoardService.updateScore(event.contestId(), event.participantId(), event.newScore());
            
            // Get updated leaderboard
            List<LeaderBoardDTO.LeaderBoardResponse> leaderboard = leaderBoardService.finalizeLeaderboard(event.contestId());
            
            // Publish leaderboard update event
            LeaderBoardDTO.LeaderBoardUpdateEvent leaderboardEvent = new LeaderBoardDTO.LeaderBoardUpdateEvent(
                    "leaderboard.updated",
                    event.contestId(),
                    leaderboard,
                    java.time.Instant.now()
            );
            
            kafkaTemplate.send("leaderboard.updated", String.valueOf(event.contestId()), leaderboardEvent);
            
        } catch (Exception e) {
            log.error("Error processing score update for leaderboard: {}", e.getMessage(), e);
        }
    }
//
//    @KafkaListener(topics = "contest.started", groupId = "leaderboard-processor", containerFactory = "kafkaListenerContainerFactory")
//    public void onContestStarted(GameEventDTO.ContestLifecycleEvent event) {
//        log.info("Initializing leaderboard for contest: {}", event.contestId());
//
//        try {
//            // Initialize leaderboard for the contest
//            leaderBoardService.initializeLeaderboard(event.contestId());
//
//        } catch (Exception e) {
//            log.error("Error initializing leaderboard for contest {}: {}", event.contestId(), e.getMessage(), e);
//        }
//    }
}
