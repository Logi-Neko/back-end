package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.kafka.processor.GameProcessor;
import exe2.learningapp.logineko.quizziz.service.kafka.processor.LeaderboardProcessor;
import exe2.learningapp.logineko.quizziz.service.kafka.processor.ParticipantProcessor;
import exe2.learningapp.logineko.quizziz.service.kafka.processor.ContestStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsConsumer {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final GameProcessor gameProcessor;
    private final LeaderboardProcessor leaderboardProcessor;
    private final ParticipantProcessor participantProcessor;
    private final ContestStateManager contestStateManager;

    // Contest Lifecycle Events
    @KafkaListener(topics = "game-events", groupId = "events-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consumeGameEvent(Object event) {
        log.info("Received game event: {}", event);
        
        try {
            // Parse event type from the event object
            String eventType = extractEventType(event);
            Long contestId = extractContestId(event);
            
            if (eventType == null || contestId == null) {
                log.warn("Invalid event format: {}", event);
                return;
            }
            
            // Route to appropriate processor based on event type
            switch (eventType) {
                case "contest.created":
                case "contest.started":
                case "contest.ended":
                    handleContestLifecycleEvent(event, eventType, contestId);
                    break;
                case "question.revealed":
                    handleQuestionRevealedEvent(event, contestId);
                    break;
                case "answer.submitted":
                    handleAnswerSubmittedEvent(event, contestId);
                    break;
                case "score.updated":
                    handleScoreUpdatedEvent(event, contestId);
                    break;
                case "leaderboard.updated":
                    handleLeaderboardUpdatedEvent(event, contestId);
                    break;
                case "leaderboard.refresh":
                    handleLeaderboardRefreshEvent(event, contestId);
                    break;
                case "participant.created":
                    handleParticipantCreatedEvent(event, contestId);
                    break;
                default:
                    log.warn("Unknown event type: {}", eventType);
            }
            
        } catch (Exception e) {
            log.error("Error processing game event: {}", e.getMessage(), e);
        }
    }

    private void handleContestLifecycleEvent(Object event, String eventType, Long contestId) {
        log.info("Processing contest lifecycle event: {} for contest {}", eventType, contestId);
        
        // Update contest state
        contestStateManager.handleContestLifecycleEvent(event, eventType, contestId);
        
        // Broadcast to WebSocket
        String destination = "/topic/contest." + contestId;
        messagingTemplate.convertAndSend(destination, event);
        
        // Also send to global contest topic for monitoring
        messagingTemplate.convertAndSend("/topic/contests", event);
    }

    private void handleQuestionRevealedEvent(Object event, Long contestId) {
        log.info("Processing question revealed event for contest {}", contestId);
        
        // Process question reveal
        gameProcessor.handleQuestionRevealed(event, contestId);
        
        // Broadcast to WebSocket
        String destination = "/topic/contest." + contestId;
        messagingTemplate.convertAndSend(destination, event);
        
        // Also send to specific question topic
        Long contestQuestionId = extractContestQuestionId(event);
        if (contestQuestionId != null) {
            String questionDestination = "/topic/contest." + contestId + ".question." + contestQuestionId;
            messagingTemplate.convertAndSend(questionDestination, event);
        }
    }

    private void handleAnswerSubmittedEvent(Object event, Long contestId) {
        log.info("Processing answer submitted event for contest {}", contestId);
        
        // Process answer submission
        gameProcessor.handleAnswerSubmitted(event, contestId);
        
        // Broadcast to WebSocket
        String destination = "/topic/contest." + contestId;
        messagingTemplate.convertAndSend(destination, event);
        
        // Also send to participant-specific topic
        Long participantId = extractParticipantId(event);
        if (participantId != null) {
            String participantDestination = "/topic/contest." + contestId + ".participant." + participantId;
            messagingTemplate.convertAndSend(participantDestination, event);
        }
    }

    private void handleScoreUpdatedEvent(Object event, Long contestId) {
        log.info("Processing score updated event for contest {}", contestId);
        
        // Process score update
        leaderboardProcessor.handleScoreUpdated(event, contestId);
        
        // Broadcast to WebSocket
        String destination = "/topic/contest." + contestId;
        messagingTemplate.convertAndSend(destination, event);
        
        // Also send to participant-specific topic
        Long participantId = extractParticipantId(event);
        if (participantId != null) {
            String participantDestination = "/topic/contest." + contestId + ".participant." + participantId;
            messagingTemplate.convertAndSend(participantDestination, event);
        }
    }

    private void handleLeaderboardUpdatedEvent(Object event, Long contestId) {
        log.info("Processing leaderboard updated event for contest {}", contestId);
        
        // Broadcast to WebSocket
        String destination = "/topic/contest." + contestId + ".leaderboard";
        messagingTemplate.convertAndSend(destination, event);
        
        // Also send to main contest topic
        String mainDestination = "/topic/contest." + contestId;
        messagingTemplate.convertAndSend(mainDestination, event);
    }

    private void handleLeaderboardRefreshEvent(Object event, Long contestId) {
        log.info("Processing leaderboard refresh event for contest {}", contestId);
        
        // Process leaderboard refresh
        leaderboardProcessor.handleLeaderboardRefresh(event, contestId);
    }

    private void handleParticipantCreatedEvent(Object event, Long contestId) {
        log.info("Processing participant created event for contest {}", contestId);
        
        // Process participant creation
        participantProcessor.handleParticipantCreated(event, contestId);
        
        // Broadcast to WebSocket
        String destination = "/topic/contest." + contestId;
        messagingTemplate.convertAndSend(destination, event);
        
        // Also send to participant-specific topic
        Long participantId = extractParticipantId(event);
        if (participantId != null) {
            String participantDestination = "/topic/contest." + contestId + ".participant." + participantId;
            messagingTemplate.convertAndSend(participantDestination, event);
        }
    }

    // Helper methods to extract data from events
    private String extractEventType(Object event) {
        try {
            if (event instanceof GameEventDTO.ContestLifecycleEvent) {
                return ((GameEventDTO.ContestLifecycleEvent) event).getEventType();
            } else if (event instanceof GameEventDTO.QuestionRevealedEvent) {
                return ((GameEventDTO.QuestionRevealedEvent) event).getEventType();
            } else if (event instanceof GameEventDTO.AnswerSubmittedEvent) {
                return ((GameEventDTO.AnswerSubmittedEvent) event).getEventType();
            } else if (event instanceof GameEventDTO.ScoreUpdatedEvent) {
                return ((GameEventDTO.ScoreUpdatedEvent) event).getEventType();
            } else if (event instanceof GameEventDTO.LeaderboardUpdatedEvent) {
                return ((GameEventDTO.LeaderboardUpdatedEvent) event).getEventType();
            } else if (event instanceof GameEventDTO.LeaderboardRefreshEvent) {
                return ((GameEventDTO.LeaderboardRefreshEvent) event).getEventType();
            } else if (event instanceof GameEventDTO.ParticipantCreatedEvent) {
                return ((GameEventDTO.ParticipantCreatedEvent) event).getEventType();
            }
        } catch (Exception e) {
            log.error("Error extracting event type: {}", e.getMessage());
        }
        return null;
    }

    private Long extractContestId(Object event) {
        try {
            if (event instanceof GameEventDTO.ContestLifecycleEvent) {
                return ((GameEventDTO.ContestLifecycleEvent) event).getContestId();
            } else if (event instanceof GameEventDTO.QuestionRevealedEvent) {
                return ((GameEventDTO.QuestionRevealedEvent) event).getContestId();
            } else if (event instanceof GameEventDTO.AnswerSubmittedEvent) {
                return ((GameEventDTO.AnswerSubmittedEvent) event).getContestId();
            } else if (event instanceof GameEventDTO.ScoreUpdatedEvent) {
                return ((GameEventDTO.ScoreUpdatedEvent) event).getContestId();
            } else if (event instanceof GameEventDTO.LeaderboardUpdatedEvent) {
                return ((GameEventDTO.LeaderboardUpdatedEvent) event).getContestId();
            } else if (event instanceof GameEventDTO.LeaderboardRefreshEvent) {
                return ((GameEventDTO.LeaderboardRefreshEvent) event).getContestId();
            } else if (event instanceof GameEventDTO.ParticipantCreatedEvent) {
                return ((GameEventDTO.ParticipantCreatedEvent) event).getContestId();
            }
        } catch (Exception e) {
            log.error("Error extracting contest ID: {}", e.getMessage());
        }
        return null;
    }

    private Long extractContestQuestionId(Object event) {
        try {
            if (event instanceof GameEventDTO.QuestionRevealedEvent) {
                return ((GameEventDTO.QuestionRevealedEvent) event).getContestQuestionId();
            } else if (event instanceof GameEventDTO.AnswerSubmittedEvent) {
                return ((GameEventDTO.AnswerSubmittedEvent) event).getContestQuestionId();
            }
        } catch (Exception e) {
            log.error("Error extracting contest question ID: {}", e.getMessage());
        }
        return null;
    }

    private Long extractParticipantId(Object event) {
        try {
            if (event instanceof GameEventDTO.AnswerSubmittedEvent) {
                return ((GameEventDTO.AnswerSubmittedEvent) event).getParticipantId();
            } else if (event instanceof GameEventDTO.ScoreUpdatedEvent) {
                return ((GameEventDTO.ScoreUpdatedEvent) event).getParticipantId();
            } else if (event instanceof GameEventDTO.ParticipantCreatedEvent) {
                return ((GameEventDTO.ParticipantCreatedEvent) event).getParticipantId();
            }
        } catch (Exception e) {
            log.error("Error extracting participant ID: {}", e.getMessage());
        }
        return null;
    }
}
