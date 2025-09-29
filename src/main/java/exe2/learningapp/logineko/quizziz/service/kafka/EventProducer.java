package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.entity.Contest;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.repository.ContestRepository;
import exe2.learningapp.logineko.quizziz.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountRepository accountRepository;
    private final ContestRepository contestRepository;
    private final ParticipantRepository participantRepository;

    private CompletableFuture<SendResult<String, Object>> sendEvent(Long contestId, Object event) {
        log.info("Publishing event {} for contest {}", event.getClass().getSimpleName(), contestId);
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("game-events", String.valueOf(contestId), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event {} sent successfully for contest {}: {}", 
                    event.getClass().getSimpleName(), contestId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send event {} for contest {}: {}", 
                    event.getClass().getSimpleName(), contestId, ex.getMessage());
            }
        });
        
        return future;
    }

    // Contest Lifecycle Events
    public CompletableFuture<SendResult<String, Object>> publishContestLifecycle(Long contestId, GameEventDTO.ContestLifecycleEvent ev) { 
        return sendEvent(contestId, ev); 
    }

    // Question Events
    public CompletableFuture<SendResult<String, Object>> publishQuestionRevealed(Long contestId, GameEventDTO.QuestionRevealedEvent ev) { 
        return sendEvent(contestId, ev); 
    }

    // Answer Events
    public CompletableFuture<SendResult<String, Object>> publishAnswerSubmitted(Long contestId, GameEventDTO.AnswerSubmittedEvent ev) { 
        return sendEvent(contestId, ev); 
    }

    // Score Events
    public CompletableFuture<SendResult<String, Object>> publishScoreUpdated(Long contestId, GameEventDTO.ScoreUpdatedEvent ev) { 
        return sendEvent(contestId, ev); 
    }

//    // Leaderboard Events
//    public CompletableFuture<SendResult<String, Object>> publishLeaderboardUpdated(Long contestId, GameEventDTO.LeaderboardUpdatedEvent ev) {
//        return sendEvent(contestId, ev);
//    }

    public CompletableFuture<SendResult<String, Object>> publishLeaderboardRefresh(Long contestId, GameEventDTO.LeaderboardRefreshEvent ev) { 
        return sendEvent(contestId, ev); 
    }

    // Participant Events
    public CompletableFuture<SendResult<String, Object>> publishParticipantCreated(Long contestId, GameEventDTO.ParticipantCreatedEvent ev) { 
        return sendEvent(contestId, ev); 
    }

    // Utility methods for common event creation
    public CompletableFuture<SendResult<String, Object>> publishContestCreated(Long contestId) {
        GameEventDTO.ContestLifecycleEvent event = GameEventDTO.ContestLifecycleEvent.builder()
                .eventType("contest.created")
                .contestId(contestId)
                .timestamp(java.time.Instant.now())
                .build();
        return publishContestLifecycle(contestId, event);
    }

    public CompletableFuture<SendResult<String, Object>> publishContestStarted(Long contestId) {
        GameEventDTO.ContestLifecycleEvent event = GameEventDTO.ContestLifecycleEvent.builder()
                .eventType("contest.started")
                .contestId(contestId)
                .timestamp(java.time.Instant.now())
                .build();
        return publishContestLifecycle(contestId, event);
    }

    public CompletableFuture<SendResult<String, Object>> publishContestEnded(Long contestId) {
        GameEventDTO.ContestLifecycleEvent event = GameEventDTO.ContestLifecycleEvent.builder()
                .eventType("contest.ended")
                .contestId(contestId)
                .timestamp(java.time.Instant.now())
                .build();
        return publishContestLifecycle(contestId, event);
    }

    public CompletableFuture<SendResult<String, Object>> publishQuestionRevealed(Long contestId, Long contestQuestionId, Integer orderIndex, Object question) {
        GameEventDTO.QuestionRevealedEvent event = GameEventDTO.QuestionRevealedEvent.builder()
                .eventType("question.revealed")
                .contestId(contestId)
                .contestQuestionId(contestQuestionId)
                .orderIndex(orderIndex)
                .question(question)
                .timestamp(java.time.Instant.now())
                .build();
        return publishQuestionRevealed(contestId, event);
    }

    public CompletableFuture<SendResult<String, Object>> publishAnswerSubmitted(Long contestId, Long participantId, Long contestQuestionId, String answer) {
        GameEventDTO.AnswerSubmittedEvent event = GameEventDTO.AnswerSubmittedEvent.builder()
                .eventType("answer.submitted")
                .contestId(contestId)
                .participantId(participantId)
                .contestQuestionId(contestQuestionId)
                .answer(answer)
                .timestamp(java.time.Instant.now())
                .build();
        return publishAnswerSubmitted(contestId, event);
    }

    public CompletableFuture<SendResult<String, Object>> publishScoreUpdated(Long contestId, Long participantId, Integer score) {
        GameEventDTO.ScoreUpdatedEvent event = GameEventDTO.ScoreUpdatedEvent.builder()
                .eventType("score.updated")
                .contestId(contestId)
                .participantId(participantId)
                .score(score)
                .timestamp(java.time.Instant.now())
                .build();
        return publishScoreUpdated(contestId, event);
    }

    public CompletableFuture<SendResult<String, Object>> publishParticipantCreated(Long contestId, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("Contest not found: " + contestId));
        // 2. Tạo participant
        Participant participant = new Participant();
        participant.setContest(contest);
        participant.setAccount(account);
        participant = participantRepository.save(participant);

        // 3. Publish event
        GameEventDTO.ParticipantCreatedEvent event = GameEventDTO.ParticipantCreatedEvent.builder()
                .eventType("participant.created")
                .contestId(contestId)
                .participantId(participant.getId())
                .name(account.getFirstName()) // hoặc account.getUsername()
                .timestamp(java.time.Instant.now())
                .build();

        return publishParticipantCreated(contestId, event);
    }
    public CompletableFuture<SendResult<String, Object>> publishLeaderboardRefresh(Long contestId) {
        GameEventDTO.LeaderboardRefreshEvent event = GameEventDTO.LeaderboardRefreshEvent.builder()
                .eventType("leaderboard.refresh")
                .contestId(contestId)
                .timestamp(java.time.Instant.now())
                .build();
        return publishLeaderboardRefresh(contestId, event);
    }
}
