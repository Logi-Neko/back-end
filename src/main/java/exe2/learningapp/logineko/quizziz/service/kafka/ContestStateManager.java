package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestStateManager {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // In-memory state management (in production, consider using Redis)
    private final Map<Long, ContestState> contestStates = new ConcurrentHashMap<>();
    
    public static class ContestState {
        private boolean started = false;
        private boolean ended = false;
        private int currentQuestionIndex = 0;
        private Instant startTime;
        private Instant endTime;
        
        // Getters and setters
        public boolean isStarted() { return started; }
        public void setStarted(boolean started) { this.started = started; }
        public boolean isEnded() { return ended; }
        public void setEnded(boolean ended) { this.ended = ended; }
        public int getCurrentQuestionIndex() { return currentQuestionIndex; }
        public void setCurrentQuestionIndex(int currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
        public Instant getStartTime() { return startTime; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public Instant getEndTime() { return endTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }
    }

    @KafkaListener(topics = "contest.lifecycle", groupId = "contest-state-manager", containerFactory = "kafkaListenerContainerFactory")
    public void onContestLifecycle(GameEventDTO.ContestLifecycleEvent event) {
        log.info("Processing contest lifecycle event: {}", event);
        
        ContestState state = contestStates.computeIfAbsent(event.contestId(), k -> new ContestState());
        
        switch (event.eventType()) {
            case "contest.started":
                state.setStarted(true);
                state.setStartTime(event.timestamp());
                log.info("Contest {} started at {}", event.contestId(), event.timestamp());
                break;
                
            case "contest.ended":
                state.setEnded(true);
                state.setEndTime(event.timestamp());
                log.info("Contest {} ended at {}", event.contestId(), event.timestamp());
                break;
                
            case "participant.joined":
                log.info("Participant {} joined contest {}", event.participantId(), event.contestId());
                break;
                
            case "participant.left":
                log.info("Participant {} left contest {}", event.participantId(), event.contestId());
                break;
        }
    }

    @KafkaListener(topics = "question.revealed", groupId = "contest-state-manager", containerFactory = "kafkaListenerContainerFactory")
    public void onQuestionRevealed(GameEventDTO.QuestionRevealedEvent event) {
        log.info("Processing question revealed event: {}", event);
        
        ContestState state = contestStates.computeIfAbsent(event.contestId(), k -> new ContestState());
        state.setCurrentQuestionIndex(event.orderIndex());
        
        log.info("Contest {} current question index: {}", event.contestId(), event.orderIndex());
    }

    public ContestState getContestState(Long contestId) {
        return contestStates.get(contestId);
    }

    public boolean isContestActive(Long contestId) {
        ContestState state = contestStates.get(contestId);
        return state != null && state.isStarted() && !state.isEnded();
    }
}
