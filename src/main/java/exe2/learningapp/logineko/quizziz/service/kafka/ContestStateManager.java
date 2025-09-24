package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestStateManager {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // In-memory state management (in production, consider using Redis)
    private final Map<Long, ContestState> contestStates = new ConcurrentHashMap<>();
    
    public static class ContestState {
        private boolean created = false;
        private boolean started = false;
        private boolean ended = false;
        private int currentQuestionIndex = 0;
        private int totalQuestions = 0;
        private String currentState = "waiting"; // waiting, question_active, question_ended, showing_results, finished
        private Instant createdTime;
        private Instant startTime;
        private Instant endTime;
        private Set<Long> participants = ConcurrentHashMap.newKeySet();
        private Map<Long, Integer> participantScores = new ConcurrentHashMap<>();
        
        // Getters and setters
        public boolean isCreated() { return created; }
        public void setCreated(boolean created) { this.created = created; }
        public boolean isStarted() { return started; }
        public void setStarted(boolean started) { this.started = started; }
        public boolean isEnded() { return ended; }
        public void setEnded(boolean ended) { this.ended = ended; }
        public int getCurrentQuestionIndex() { return currentQuestionIndex; }
        public void setCurrentQuestionIndex(int currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
        public int getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
        public String getCurrentState() { return currentState; }
        public void setCurrentState(String currentState) { this.currentState = currentState; }
        public Instant getCreatedTime() { return createdTime; }
        public void setCreatedTime(Instant createdTime) { this.createdTime = createdTime; }
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
        public Set<Long> getParticipants() { return participants; }
        public Map<Long, Integer> getParticipantScores() { return participantScores; }
    }

    @KafkaListener(topics = "contest.lifecycle", groupId = "contest-state-manager", containerFactory = "kafkaListenerContainerFactory")
    public void onContestLifecycle(GameEventDTO.ContestLifecycleEvent event) {
        log.info("Processing contest lifecycle event: {}", event);
        
        ContestState state = contestStates.computeIfAbsent(event.contestId(), k -> new ContestState());
        
        switch (event.eventType()) {
            case "contest.created":
                state.setCreated(true);
                state.setCreatedTime(event.timestamp());
                log.info("Contest {} created at {}", event.contestId(), event.timestamp());
                break;
                
            case "contest.started":
                state.setStarted(true);
                state.setStartTime(event.timestamp());
                state.setCurrentState("waiting");
                log.info("Contest {} started at {}", event.contestId(), event.timestamp());
                break;
                
            case "contest.ended":
                state.setEnded(true);
                state.setEndTime(event.timestamp());
                state.setCurrentState("finished");
                log.info("Contest {} ended at {}", event.contestId(), event.timestamp());
                break;
                
            case "participant.joined":
                if (event.participantId() != null) {
                    state.getParticipants().add(event.participantId());
                    state.getParticipantScores().put(event.participantId(), 0);
                    log.info("Participant {} joined contest {} (total participants: {})", 
                            event.participantId(), event.contestId(), state.getParticipants().size());
                }
                break;
                
            case "participant.left":
                if (event.participantId() != null) {
                    state.getParticipants().remove(event.participantId());
                    state.getParticipantScores().remove(event.participantId());
                    log.info("Participant {} left contest {} (remaining participants: {})", 
                            event.participantId(), event.contestId(), state.getParticipants().size());
                }
                break;
        }
    }

    @KafkaListener(topics = "question.revealed", groupId = "contest-state-manager", containerFactory = "kafkaListenerContainerFactory")
    public void onQuestionRevealed(GameEventDTO.QuestionRevealedEvent event) {
        log.info("Processing question revealed event: {}", event);
        
        ContestState state = contestStates.computeIfAbsent(event.contestId(), k -> new ContestState());
        state.setCurrentQuestionIndex(event.orderIndex());
        state.setCurrentState("question_active");
        
        log.info("Contest {} current question index: {} (state: {})", 
                event.contestId(), event.orderIndex(), state.getCurrentState());
    }

    @KafkaListener(topics = "question.ended", groupId = "contest-state-manager", containerFactory = "kafkaListenerContainerFactory")
    public void onQuestionEnded(GameEventDTO.QuestionEndedEvent event) {
        log.info("Processing question ended event: {}", event);
        
        ContestState state = contestStates.computeIfAbsent(event.contestId(), k -> new ContestState());
        state.setCurrentState("showing_results");
        
        log.info("Contest {} question {} ended (state: {})", 
                event.contestId(), event.orderIndex(), state.getCurrentState());
    }

    @KafkaListener(topics = "score.updated", groupId = "contest-state-manager", containerFactory = "kafkaListenerContainerFactory")
    public void onScoreUpdated(GameEventDTO.ScoreUpdatedEvent event) {
        log.info("Processing score updated event: {}", event);
        
        ContestState state = contestStates.computeIfAbsent(event.contestId(), k -> new ContestState());
        state.getParticipantScores().put(event.participantId(), event.newScore());
        
        log.info("Participant {} score updated to {} in contest {}", 
                event.participantId(), event.newScore(), event.contestId());
    }

    @KafkaListener(topics = "game.state.changed", groupId = "contest-state-manager", containerFactory = "kafkaListenerContainerFactory")
    public void onGameStateChanged(GameEventDTO.GameStateChangedEvent event) {
        log.info("Processing game state changed event: {}", event);
        
        ContestState state = contestStates.computeIfAbsent(event.contestId(), k -> new ContestState());
        state.setCurrentState(event.state());
        state.setCurrentQuestionIndex(event.currentQuestionIndex());
        state.setTotalQuestions(event.totalQuestions());
        
        log.info("Contest {} game state changed to: {} (question {}/{})", 
                event.contestId(), event.state(), event.currentQuestionIndex(), event.totalQuestions());
    }

    public ContestState getContestState(Long contestId) {
        return contestStates.get(contestId);
    }

    public boolean isContestActive(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null && state.isStarted() && !state.isEnded();
    }

    public boolean isContestCreated(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null && state.isCreated();
    }

    public boolean canAcceptAnswers(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null && state.isStarted() && !state.isEnded() && 
               "question_active".equals(state.getCurrentState());
    }

    public int getParticipantCount(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null ? state.getParticipants().size() : 0;
    }

    public Map<Long, Integer> getParticipantScores(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null ? state.getParticipantScores() : new ConcurrentHashMap<>();
    }
}
