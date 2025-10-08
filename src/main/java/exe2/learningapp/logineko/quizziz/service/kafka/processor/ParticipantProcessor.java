package exe2.learningapp.logineko.quizziz.service.kafka.processor;

import com.fasterxml.jackson.databind.JsonNode;
import exe2.learningapp.logineko.quizziz.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantProcessor {
    
    private final ParticipantService participantService;

    public void handleParticipantCreated(Object event, Long contestId) {
        log.info("👤 Processing participant created event for contest {}", contestId);
        
        try {
            JsonNode jsonEvent = (JsonNode) event;
            
            if (jsonEvent.has("participantId")) {
                Long participantId = jsonEvent.get("participantId").asLong();
                String participantName = jsonEvent.has("name") ? jsonEvent.get("name").asText() : "Unknown";
                
                log.info("✅ Participant {} created for contest {} with name: {}", 
                    participantId, contestId, participantName);
                
                // Note: Participant creation and leaderboard initialization is now handled in EventProducer
                // This processor just logs the event for monitoring and debugging
            }
        } catch (Exception e) {
            log.error("❌ Error processing participant created event: {}", e.getMessage(), e);
        }
    }

    public void handleParticipantJoined(Object event, Long contestId) {
        log.info("Processing participant joined for contest {}", contestId);
        
        try {
            // Handle participant joining logic
            // This could include validation, notifications, etc.
            
            log.info("Participant joined contest {}", contestId);
        } catch (Exception e) {
            log.error("Error processing participant joined event: {}", e.getMessage(), e);
        }
    }

    public void handleParticipantLeft(Object event, Long contestId) {
        log.info("Processing participant left for contest {}", contestId);
        
        try {
            // Handle participant leaving logic
            // This could include cleanup, notifications, etc.
            
            log.info("Participant left contest {}", contestId);
        } catch (Exception e) {
            log.error("Error processing participant left event: {}", e.getMessage(), e);
        }
    }

    public void handleParticipantScoreUpdate(Object event, Long contestId) {
        log.info("Processing participant score update for contest {}", contestId);
        
        try {
            JsonNode jsonEvent = (JsonNode) event;
            
            if (jsonEvent.has("participantId") && jsonEvent.has("score")) {
                Long participantId = jsonEvent.get("participantId").asLong();
                Integer score = jsonEvent.get("score").asInt();
                
                // Update participant score in database
                participantService.incrementScore(participantId, score);
                
                log.info("Participant {} score updated in contest {} to {}", 
                    participantId, contestId, score);
            }
        } catch (Exception e) {
            log.error("Error processing participant score update: {}", e.getMessage(), e);
        }
    }
}
