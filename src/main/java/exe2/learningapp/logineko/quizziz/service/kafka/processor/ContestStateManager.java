package exe2.learningapp.logineko.quizziz.service.kafka.processor;

import exe2.learningapp.logineko.quizziz.service.ContestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestStateManager {
    
    private final LeaderboardProcessor leaderboardProcessor;
    private final ContestService contestService;
    
    // In-memory state management (in production, consider using Redis or database)
    private final Map<Long, ContestState> contestStates = new ConcurrentHashMap<>();

    public void handleContestLifecycleEvent(Object event, String eventType, Long contestId) {
        log.info("Processing contest lifecycle event: {} for contest {}", eventType, contestId);
        
        try {
            switch (eventType) {
                case "contest.created":
                    handleContestCreated(event, contestId);
                    break;
                case "contest.started":
                    handleContestStarted(event, contestId);
                    break;
                case "contest.ended":
                    handleContestEnded(event, contestId);
                    break;
                default:
                    log.warn("Unknown contest lifecycle event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing contest lifecycle event: {}", e.getMessage(), e);
        }
    }

    private void handleContestCreated(Object event, Long contestId) {
        log.info("Handling contest created for contest {}", contestId);
        
        try {
            // Check if contest already exists in database
            var existingContest = contestService.findById(contestId);
            if (existingContest.isPresent()) {
                log.warn("Contest {} already exists in database", contestId);
            } else {
                log.info("Contest {} does not exist in database, creating new state only", contestId);
            }
            
            // Initialize contest state
            ContestState state = new ContestState();
            state.setContestId(contestId);
            state.setStatus("OPEN");
            state.setCreatedAt(Instant.now());
            contestStates.put(contestId, state);
            
            log.info("Contest {} created successfully with state: {}", contestId, state.getStatus());
        } catch (Exception e) {
            log.error("Error handling contest created: {}", e.getMessage(), e);
        }
    }

    private void handleContestStarted(Object event, Long contestId) {
        log.info("Handling contest started for contest {}", contestId);
        
        try {
            // Validate contest exists in database
            var contest = contestService.findById(contestId);
            if (contest.isEmpty()) {
                log.error("Cannot start contest {} - not found in database", contestId);
                return;
            }
            
            // Update contest state
            ContestState state = contestStates.get(contestId);
            if (state != null) {
                if ("OPEN".equals(state.getStatus())) {
                    log.warn("Contest {} is already started", contestId);
                    return;
                }
                state.setStatus("RUNNING");
                state.setStartedAt(Instant.now());
            } else {
                // Create new state if not exists
                state = new ContestState();
                state.setContestId(contestId);
                state.setStatus("RUNNING");
                state.setStartedAt(Instant.now());
                contestStates.put(contestId, state);
            }
            
            // Update contest in database
            contestService.startContest(contestId);
            
            // Initialize leaderboard
            leaderboardProcessor.handleContestStarted(event, contestId);
            
            log.info("Contest {} started successfully with state: {}", contestId, state.getStatus());
        } catch (Exception e) {
            log.error("Error handling contest started: {}", e.getMessage(), e);
        }
    }

    private void handleContestEnded(Object event, Long contestId) {
        log.info("Handling contest ended for contest {}", contestId);
        
        try {
            // Validate contest exists in database
            var contest = contestService.findById(contestId);
            if (contest.isEmpty()) {
                log.error("Cannot end contest {} - not found in database", contestId);
                return;
            }
            
            // Update contest state
            ContestState state = contestStates.get(contestId);
            if (state != null) {
                if ("ended".equals(state.getStatus())) {
                    log.warn("Contest {} is already ended", contestId);
                    return;
                }
                state.setStatus("ended");
                state.setEndedAt(Instant.now());
            } else {
                // Create new state if not exists
                state = new ContestState();
                state.setContestId(contestId);
                state.setStatus("ended");
                state.setEndedAt(Instant.now());
                contestStates.put(contestId, state);
            }
            
            // Update contest in database
            contestService.endContest(contestId);
            
            // Finalize leaderboard
            leaderboardProcessor.handleContestEnded(event, contestId);
            
            log.info("Contest {} ended successfully with state: {}", contestId, state.getStatus());
        } catch (Exception e) {
            log.error("Error handling contest ended: {}", e.getMessage(), e);
        }
    }

    public ContestState getContestState(Long contestId) {
        return contestStates.get(contestId);
    }

    public boolean isContestActive(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null && "RUNNING".equals(state.getStatus());
    }

    public boolean isContestEnded(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null && "ended".equals(state.getStatus());
    }

    // Inner class for contest state
    public static class ContestState {
        private Long contestId;
        private String status; // created, started, ended
        private Instant createdAt;
        private Instant startedAt;
        private Instant endedAt;
        private Integer currentQuestionIndex = 0;
        private Integer totalQuestions = 0;

        // Getters and setters
        public Long getContestId() { return contestId; }
        public void setContestId(Long contestId) { this.contestId = contestId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        
        public Instant getStartedAt() { return startedAt; }
        public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
        
        public Instant getEndedAt() { return endedAt; }
        public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }
        
        public Integer getCurrentQuestionIndex() { return currentQuestionIndex; }
        public void setCurrentQuestionIndex(Integer currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
        
        public Integer getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    }
}
