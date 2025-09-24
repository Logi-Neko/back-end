package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameProcessor {
    private final AnswerService answerService;
    private final ParticipantService participantService;
    private final ContestQuestionService contestQuestionService;
    private final AnswerOptionService answerOptionService;
    private final QuestionService questionService;
    private final LeaderBoardService leaderboardService;
    private final ContestService contestService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "answer.submitted", groupId = "game-service")
    @Transactional
    public void onAnswerSubmitted(GameEventDTO.AnswerSubmittedEvent ev) {
        log.info("Processing answer submitted event: {}", ev);
        
        try {
            Long contestId = ev.contestId();
            
            // Idempotency check
            if (answerService.existsBySubmissionUuid(ev.submissionUuid())) {
                log.info("Answer already processed for submission UUID: {}", ev.submissionUuid());
                return;
            }

            // Load required entities
            var contestQuestion = contestQuestionService.findById(ev.contestQuestionId())
                    .orElseThrow(() -> new RuntimeException("ContestQuestion not found"));
            var participant = participantService.findById(ev.participantId())
                    .orElseThrow(() -> new RuntimeException("Participant not found"));
            var selectedOption = answerOptionService.findById(ev.answerOptionId())
                    .orElseThrow(() -> new RuntimeException("AnswerOption not found"));
            
            // Check if answer is correct
            boolean isCorrect = selectedOption.isCorrect() != null && selectedOption.isCorrect();
            
            // Calculate score based on correctness and speed
            var question = questionService.findById(contestQuestion.questionId());
            int basePoints = question.points() != null ? question.points() : 1000;
            int timeSeconds = (int) (ev.timeTakenMs() / 1000);
            int score = isCorrect ? Math.max(0, basePoints - timeSeconds * 10) : 0;

            // Persist answer
            answerService.saveFromEvent(ev.submissionUuid(), participant.id(), contestQuestion.id(), selectedOption.id(), isCorrect, score, timeSeconds);

            // Update participant total score
            int newTotal = participantService.incrementScore(participant.id(), score);

            // Compute rank
            int rank = leaderboardService.computeRank(contestId, participant.id());

            // Publish score updated event
            GameEventDTO.ScoreUpdatedEvent scoreEv = GameEventDTO.ScoreUpdatedEvent.builder()
                    .eventType("score.updated")
                    .contestId(contestId)
                    .participantId(participant.id())
                    .newScore(newTotal)
                    .rank(rank)
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("score.updated", String.valueOf(contestId), scoreEv);
            
            log.info("Answer processed successfully for participant {} in contest {}", participant.id(), contestId);
            
        } catch (Exception e) {
            log.error("Error processing answer submitted event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "question.revealed", groupId = "game-service")
    public void onQuestionRevealed(GameEventDTO.QuestionRevealedEvent event) {
        log.info("Processing question revealed event: {}", event);
        
        try {
            // Update game state to question_active
            GameEventDTO.GameStateChangedEvent stateEvent = GameEventDTO.GameStateChangedEvent.builder()
                    .eventType("game.state.changed")
                    .contestId(event.contestId())
                    .state("question_active")
                    .currentQuestionIndex(event.orderIndex())
                    .totalQuestions(getTotalQuestionsForContest(event.contestId()))
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("game.state.changed", String.valueOf(event.contestId()), stateEvent);
            
            // Send notification to all participants
            GameEventDTO.NotificationEvent notification = GameEventDTO.NotificationEvent.builder()
                    .eventType("notification")
                    .contestId(event.contestId())
                    .participantId(null) // broadcast
                    .message("New question revealed!")
                    .type("info")
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("notification", String.valueOf(event.contestId()), notification);
            
        } catch (Exception e) {
            log.error("Error processing question revealed event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "question.ended", groupId = "game-service")
    public void onQuestionEnded(GameEventDTO.QuestionEndedEvent event) {
        log.info("Processing question ended event: {}", event);
        
        try {
            // Update game state to showing_results
            GameEventDTO.GameStateChangedEvent stateEvent = GameEventDTO.GameStateChangedEvent.builder()
                    .eventType("game.state.changed")
                    .contestId(event.contestId())
                    .state("showing_results")
                    .currentQuestionIndex(event.orderIndex())
                    .totalQuestions(getTotalQuestionsForContest(event.contestId()))
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("game.state.changed", String.valueOf(event.contestId()), stateEvent);
            
            // Send notification about question results
            GameEventDTO.NotificationEvent notification = GameEventDTO.NotificationEvent.builder()
                    .eventType("notification")
                    .contestId(event.contestId())
                    .participantId(null) // broadcast
                    .message("Question time ended! Results will be shown shortly.")
                    .type("info")
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("notification", String.valueOf(event.contestId()), notification);
            
        } catch (Exception e) {
            log.error("Error processing question ended event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "contest.started", groupId = "game-service")
    public void onContestStarted(GameEventDTO.ContestLifecycleEvent event) {
        log.info("Processing contest started event: {}", event);
        
        try {
            // Initialize leaderboard for the contest
            leaderboardService.initializeLeaderboard(event.contestId());
            
            // Update game state to waiting
            GameEventDTO.GameStateChangedEvent stateEvent = GameEventDTO.GameStateChangedEvent.builder()
                    .eventType("game.state.changed")
                    .contestId(event.contestId())
                    .state("waiting")
                    .currentQuestionIndex(0)
                    .totalQuestions(getTotalQuestionsForContest(event.contestId()))
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("game.state.changed", String.valueOf(event.contestId()), stateEvent);
            
            // Send welcome notification
            GameEventDTO.NotificationEvent notification = GameEventDTO.NotificationEvent.builder()
                    .eventType("notification")
                    .contestId(event.contestId())
                    .participantId(null) // broadcast
                    .message("Contest has started! Get ready for the questions.")
                    .type("success")
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("notification", String.valueOf(event.contestId()), notification);
            
        } catch (Exception e) {
            log.error("Error processing contest started event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "contest.ended", groupId = "game-service")
    public void onContestEnded(GameEventDTO.ContestLifecycleEvent event) {
        log.info("Processing contest ended event: {}", event);
        
        try {
            // Finalize leaderboard
            List<Object> finalLeaderboard = leaderboardService.finalizeLeaderboard(event.contestId());
            
            // Update game state to finished
            GameEventDTO.GameStateChangedEvent stateEvent = GameEventDTO.GameStateChangedEvent.builder()
                    .eventType("game.state.changed")
                    .contestId(event.contestId())
                    .state("finished")
                    .currentQuestionIndex(getTotalQuestionsForContest(event.contestId()))
                    .totalQuestions(getTotalQuestionsForContest(event.contestId()))
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("game.state.changed", String.valueOf(event.contestId()), stateEvent);
            
            // Publish final results
            GameEventDTO.ContestResultsEvent resultsEvent = GameEventDTO.ContestResultsEvent.builder()
                    .eventType("contest.results")
                    .contestId(event.contestId())
                    .finalLeaderboard(finalLeaderboard)
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("contest.results", String.valueOf(event.contestId()), resultsEvent);
            
            // Send final notification
            GameEventDTO.NotificationEvent notification = GameEventDTO.NotificationEvent.builder()
                    .eventType("notification")
                    .contestId(event.contestId())
                    .participantId(null) // broadcast
                    .message("Contest has ended! Check the final results.")
                    .type("info")
                    .timestamp(Instant.now())
                    .build();
            
            kafkaTemplate.send("notification", String.valueOf(event.contestId()), notification);
            
        } catch (Exception e) {
            log.error("Error processing contest ended event: {}", e.getMessage(), e);
        }
    }

    private int getTotalQuestionsForContest(Long contestId) {
        try {
            // This should be implemented in ContestQuestionService
            return contestQuestionService.getTotalQuestionsForContest(contestId);
        } catch (Exception e) {
            log.warn("Could not get total questions for contest {}, defaulting to 0", contestId);
            return 0;
        }
    }
}

