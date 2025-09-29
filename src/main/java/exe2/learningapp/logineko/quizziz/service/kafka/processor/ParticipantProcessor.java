package exe2.learningapp.logineko.quizziz.service.kafka.processor;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.ParticipantService;
import exe2.learningapp.logineko.quizziz.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantProcessor {
    
    private final ParticipantService participantService;
    private final LeaderBoardService leaderBoardService;

    public void handleParticipantCreated(Object event, Long contestId) {
        log.info("Processing participant created for contest {}", contestId);
        
        try {
            if (event instanceof GameEventDTO.ParticipantCreatedEvent) {
                GameEventDTO.ParticipantCreatedEvent participantEvent = (GameEventDTO.ParticipantCreatedEvent) event;
                
                // Check if participant already exists
                var existingParticipant = participantService.findById(participantEvent.getParticipantId());
                if (existingParticipant.isPresent()) {
                    log.warn("Participant {} already exists for contest {}", 
                        participantEvent.getParticipantId(), contestId);
                    return;
                }
                
                // Create participant in database
                // Note: The actual implementation would need accountId, but we're using participantId as accountId for now
                var participant = participantService.createParticipant(
                    participantEvent.getContestId(), 
                    participantEvent.getParticipantId()
                );
                
                // Initialize participant in leaderboard with 0 score
                leaderBoardService.updateScore(
                    participantEvent.getContestId(), 
                    participantEvent.getParticipantId(), 
                    0
                );
                
                log.info("Participant {} created for contest {} with name: {} and entity ID: {}", 
                    participantEvent.getParticipantId(), contestId, participantEvent.getName(), participant.getId());
            }
        } catch (Exception e) {
            log.error("Error processing participant created event: {}", e.getMessage(), e);
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
            if (event instanceof GameEventDTO.ScoreUpdatedEvent) {
                GameEventDTO.ScoreUpdatedEvent scoreEvent = (GameEventDTO.ScoreUpdatedEvent) event;
                
                // Update participant score in database
                participantService.incrementScore(scoreEvent.getParticipantId(), scoreEvent.getScore());
                
                log.info("Participant {} score updated in contest {} to {}", 
                    scoreEvent.getParticipantId(), contestId, scoreEvent.getScore());
            }
        } catch (Exception e) {
            log.error("Error processing participant score update: {}", e.getMessage(), e);
        }
    }
}
