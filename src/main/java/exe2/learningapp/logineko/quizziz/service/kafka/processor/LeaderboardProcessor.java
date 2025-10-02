package exe2.learningapp.logineko.quizziz.service.kafka.processor;

import com.fasterxml.jackson.databind.JsonNode;
import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import exe2.learningapp.logineko.quizziz.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardProcessor {
    
    private final LeaderBoardService leaderBoardService;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void handleScoreUpdated(Object event, Long contestId) {
        log.info("📊 Processing score update for leaderboard in contest {}", contestId);
        
        try {
            JsonNode jsonEvent = (JsonNode) event;
            
            if (jsonEvent.has("participantId") && jsonEvent.has("score")) {
                Long participantId = jsonEvent.get("participantId").asLong();
                Integer newScore = jsonEvent.get("score").asInt();
                
                // Get updated leaderboard (score update already handled in EventProducer)
                List<LeaderBoardDTO.LeaderBoardResponse> leaderboard = leaderBoardService.getLeaderboard(contestId);
                
                // Find participant rank
                int participantRank = leaderboard.stream()
                    .filter(lb -> lb.participantId().equals(participantId))
                    .mapToInt(lb -> leaderboard.indexOf(lb) + 1)
                    .findFirst()
                    .orElse(-1);
                
                // Publish leaderboard update event
                GameEventDTO.LeaderboardUpdatedEvent leaderboardEvent = GameEventDTO.LeaderboardUpdatedEvent.builder()
                        .eventType("leaderboard.updated")
                        .contestId(contestId)
                        .leaderboard(leaderboard)
                        .timestamp(Instant.now())
                        .build();
                
                // Broadcast directly via WebSocket for immediate real-time updates
                String destination = "/topic/contest." + contestId + ".leaderboard";
                messagingTemplate.convertAndSend(destination, leaderboardEvent);
                
                // Also send to main contest topic for general updates
                String mainDestination = "/topic/contest." + contestId;
                messagingTemplate.convertAndSend(mainDestination, leaderboardEvent);
                
                log.info("✅ Leaderboard updated for contest {} - Participant {}: Score {}, Rank {} ({} total participants)", 
                    contestId, participantId, newScore, participantRank, leaderboard.size());
            }
        } catch (Exception e) {
            log.error("❌ Error processing score update for leaderboard: {}", e.getMessage(), e);
        }
    }

    public void handleLeaderboardRefresh(Object event, Long contestId) {
        log.info("Processing leaderboard refresh for contest: {}", contestId);

        try {
            if (event instanceof GameEventDTO.LeaderboardRefreshEvent) {
                // Get current leaderboard
                List<LeaderBoardDTO.LeaderBoardResponse> leaderboard = leaderBoardService.getLeaderboard(contestId);
                
                // Create leaderboard update event
                GameEventDTO.LeaderboardUpdatedEvent leaderboardEvent = GameEventDTO.LeaderboardUpdatedEvent.builder()
                        .eventType("leaderboard.updated")
                        .contestId(contestId)
                        .leaderboard(leaderboard)
                        .timestamp(Instant.now())
                        .build();
                
                // Send to Kafka for other services to consume
                kafkaTemplate.send("game-events", String.valueOf(contestId), leaderboardEvent);
                
                // Also broadcast directly via WebSocket for immediate real-time updates
                String destination = "/topic/contest." + contestId + ".leaderboard";
                messagingTemplate.convertAndSend(destination, leaderboardEvent);
                
                // Also send to main contest topic for general updates
                String mainDestination = "/topic/contest." + contestId;
                messagingTemplate.convertAndSend(mainDestination, leaderboardEvent);
                
                log.info("Leaderboard refreshed and broadcasted for contest {} with {} participants", 
                    contestId, leaderboard.size());
            }
        } catch (Exception e) {
            log.error("Error refreshing leaderboard for contest {}: {}", contestId, e.getMessage(), e);
        }
    }

    public void handleContestStarted(Object event, Long contestId) {
        log.info("Initializing leaderboard for contest: {}", contestId);

        try {
            // Initialize leaderboard for the contest
            leaderBoardService.initializeLeaderboard(contestId);
            
            log.info("Leaderboard initialized for contest {}", contestId);

        } catch (Exception e) {
            log.error("Error initializing leaderboard for contest {}: {}", contestId, e.getMessage(), e);
        }
    }

    public void handleContestEnded(Object event, Long contestId) {
        log.info("Finalizing leaderboard for contest: {}", contestId);

        try {
            // Finalize leaderboard for the contest
            List<LeaderBoardDTO.LeaderBoardResponse> finalLeaderboard = leaderBoardService.finalizeLeaderboard(contestId);
            
            // Publish final leaderboard event
            GameEventDTO.LeaderboardUpdatedEvent leaderboardEvent = GameEventDTO.LeaderboardUpdatedEvent.builder()
                    .eventType("leaderboard.final")
                    .contestId(contestId)
                    .leaderboard(finalLeaderboard)
                    .timestamp(Instant.now())
                    .build();
            
            // Send to Kafka for other services to consume
            kafkaTemplate.send("game-events", String.valueOf(contestId), leaderboardEvent);
            
            // Also broadcast directly via WebSocket for immediate real-time updates
            String destination = "/topic/contest." + contestId + ".leaderboard";
            messagingTemplate.convertAndSend(destination, leaderboardEvent);
            
            // Also send to main contest topic for general updates
            String mainDestination = "/topic/contest." + contestId;
            messagingTemplate.convertAndSend(mainDestination, leaderboardEvent);
            
            // Send to results topic for final results
            String resultsDestination = "/topic/contest." + contestId + ".results";
            messagingTemplate.convertAndSend(resultsDestination, leaderboardEvent);
            
            log.info("Final leaderboard published and broadcasted for contest {} with {} participants", 
                contestId, finalLeaderboard.size());

        } catch (Exception e) {
            log.error("Error finalizing leaderboard for contest {}: {}", contestId, e.getMessage(), e);
        }
    }

    // Removed getCurrentParticipantScore method as it's no longer needed
}
