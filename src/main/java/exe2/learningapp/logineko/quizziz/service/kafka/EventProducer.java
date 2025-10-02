package exe2.learningapp.logineko.quizziz.service.kafka;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;
import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.entity.*;
import exe2.learningapp.logineko.quizziz.repository.ContestQuestionRepository;
import exe2.learningapp.logineko.quizziz.repository.ContestRepository;
import exe2.learningapp.logineko.quizziz.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    
    // Kafka
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Services for business logic
    private final ParticipantService participantService;
    private final ContestQuestionService contestQuestionService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final LeaderBoardService leaderBoardService;
    
    // Repositories for direct access when needed
    private final ContestQuestionRepository contestQuestionRepository;
    private final ContestRepository contestRepository;

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

    @Transactional
    public CompletableFuture<SendResult<String, Object>> publishContestStarted(Long contestId) {
        log.info("🚀 Publishing contest started event for contest {}", contestId);
        
        try {
            // Validate contest exists and can be started
            Contest contest = contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found with ID: " + contestId));
            
            if (!"OPEN".equals(contest.getStatus().toString())) {
                throw new IllegalStateException("Contest " + contestId + " is not in OPEN state. Current state: " + contest.getStatus());
            }
            
            // Check if contest has questions
            var questions = contestQuestionService.findByContest(contestId);
            if (questions.isEmpty()) {
                throw new IllegalStateException("Contest " + contestId + " has no questions");
            }
            
            // Initialize leaderboard for all existing participants
            initializeLeaderboardForContest(contestId);
            
            // Update contest status in database directly to avoid circular call
            Contest contestToUpdate = contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found"));
            contestToUpdate.setStatus(Contest.Status.RUNNING);
            contestToUpdate.setStartTime(LocalDateTime.now());
            contestRepository.save(contestToUpdate);
            
            GameEventDTO.ContestLifecycleEvent event = GameEventDTO.ContestLifecycleEvent.builder()
                    .eventType("contest.started")
                    .contestId(contestId)
                    .timestamp(Instant.now())
                    .build();
            
            log.info("✅ Contest {} started successfully with {} questions and initialized leaderboard", 
                contestId, questions.size());
            
            return publishContestLifecycle(contestId, event);
            
        } catch (Exception e) {
            log.error("❌ Error starting contest {}: {}", contestId, e.getMessage(), e);
            throw new RuntimeException("Failed to start contest " + contestId + ": " + e.getMessage(), e);
        }
    }

    @Transactional
    public CompletableFuture<SendResult<String, Object>> publishContestEnded(Long contestId) {
        log.info("🏁 Publishing contest ended event for contest {}", contestId);
        
        try {
            // Validate contest exists
            contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found with ID: " + contestId));
            
            // Finalize leaderboard and get final results
            List<LeaderBoardDTO.LeaderBoardResponse> finalLeaderboard = leaderBoardService.finalizeLeaderboard(contestId);
            
            // Update contest status in database directly to avoid circular call
            Contest contestToUpdate = contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found"));
            contestToUpdate.setStatus(Contest.Status.CLOSED);
            contestToUpdate.setEndTime(LocalDateTime.now());
            contestRepository.save(contestToUpdate);
            
            GameEventDTO.ContestLifecycleEvent event = GameEventDTO.ContestLifecycleEvent.builder()
                    .eventType("contest.ended")
                    .contestId(contestId)
                    .timestamp(Instant.now())
                    .build();
            
            log.info("✅ Contest {} ended successfully with {} participants in final leaderboard", 
                contestId, finalLeaderboard.size());
            
            // Also publish final leaderboard event
            publishFinalLeaderboard(contestId, finalLeaderboard);
            
            return publishContestLifecycle(contestId, event);
            
        } catch (Exception e) {
            log.error("❌ Error ending contest {}: {}", contestId, e.getMessage(), e);
            throw new RuntimeException("Failed to end contest " + contestId + ": " + e.getMessage(), e);
        }
    }

    public CompletableFuture<SendResult<String, Object>> publishQuestionRevealed(Long contestQuestionId) {
        log.info("📝 Publishing question revealed event for contest question {}", contestQuestionId);
        
        try {
            // Validate contest question exists
            ContestQuestion cq = contestQuestionRepository.findById(contestQuestionId)
                    .orElseThrow(() -> new RuntimeException("ContestQuestion not found with ID: " + contestQuestionId));
            
            // Validate contest is running
            Contest contest = cq.getContest();
            if (!"RUNNING".equals(contest.getStatus().toString())) {
                throw new IllegalStateException("Cannot reveal question for contest " + contest.getId() + 
                    ". Contest is not running. Current state: " + contest.getStatus());
            }
            
            // Get question details
            QuestionDTO.QuestionResponse dto = toResponse(cq.getQuestion());
            
            GameEventDTO.QuestionRevealedEvent event = GameEventDTO.QuestionRevealedEvent.builder()
                    .eventType("question.revealed")
                    .contestId(cq.getContest().getId())
                    .contestQuestionId(contestQuestionId)
                    .orderIndex(cq.getIndex())
                    .question(dto)
                    .timestamp(Instant.now())
                    .build();
            
            log.info("✅ Question {} revealed for contest {} at index {}", 
                contestQuestionId, contest.getId(), cq.getIndex());
            
            return publishQuestionRevealed(cq.getContest().getId(), event);
            
        } catch (Exception e) {
            log.error("❌ Error revealing question {}: {}", contestQuestionId, e.getMessage(), e);
            throw new RuntimeException("Failed to reveal question " + contestQuestionId + ": " + e.getMessage(), e);
        }
    }

    @Transactional
    public CompletableFuture<SendResult<String, Object>> publishAnswerSubmitted(Long contestId, Long participantId, Long contestQuestionId, String answer) {
        log.info("📤 Publishing answer submitted event for participant {} in contest {}", participantId, contestId);
        
        try {
            // Validate contest is running
            Contest contest = contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found with ID: " + contestId));
            
            if (!"RUNNING".equals(contest.getStatus().toString())) {
                throw new IllegalStateException("Cannot submit answer for contest " + contestId + 
                    ". Contest is not running. Current state: " + contest.getStatus());
            }
            
            // Validate participant exists
            var participantOpt = participantService.findById(participantId);
            if (participantOpt.isEmpty()) {
                throw new RuntimeException("Participant not found with ID: " + participantId);
            }
            
            // Validate contest question exists
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                throw new RuntimeException("Contest question not found with ID: " + contestQuestionId);
            }
            
            // Validate answer is not empty
            if (answer == null || answer.trim().isEmpty()) {
                throw new IllegalArgumentException("Answer cannot be empty");
            }
            
            // Generate submission UUID to prevent duplicates
            Long submissionUuid = generateSubmissionUuid(participantId, contestQuestionId, System.currentTimeMillis());
            
            // Check for duplicate submission
            if (answerService.existsBySubmissionUuid(submissionUuid)) {
                log.warn("⚠️ Duplicate answer submission detected for participant {} on question {}", 
                    participantId, contestQuestionId);
                throw new IllegalStateException("Duplicate answer submission detected");
            }
            
            // Calculate score and save answer
            ScoreCalculationResult scoreResult = calculateAnswerScore(contestQuestionId, answer.trim());
            
            // Save answer to database
            answerService.saveFromEvent(
                submissionUuid,
                participantId,
                contestQuestionId,
                parseAnswerOptionId(answer.trim()),
                scoreResult.isCorrect(),
                scoreResult.getScore(),
                scoreResult.getAnswerTime()
            );
            
            // Update leaderboard
            leaderBoardService.updateScore(contestId, participantId, scoreResult.getScore());
            
            // Create and publish answer submitted event
            GameEventDTO.AnswerSubmittedEvent event = GameEventDTO.AnswerSubmittedEvent.builder()
                    .eventType("answer.submitted")
                    .contestId(contestId)
                    .participantId(participantId)
                    .contestQuestionId(contestQuestionId)
                    .answer(answer.trim())
                    .timestamp(Instant.now())
                    .build();
            
            // Also publish score updated event
            publishScoreUpdatedInternal(contestId, participantId, scoreResult);
            
            log.info("✅ Answer submitted and processed for participant {} in contest {} - Score: {} (Correct: {})", 
                participantId, contestId, scoreResult.getScore(), scoreResult.isCorrect());
            
            return publishAnswerSubmitted(contestId, event);
            
        } catch (Exception e) {
            log.error("❌ Error processing answer submission for participant {} in contest {}: {}", 
                participantId, contestId, e.getMessage(), e);
            throw new RuntimeException("Failed to submit answer: " + e.getMessage(), e);
        }
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

    @Transactional
    public CompletableFuture<SendResult<String, Object>> publishParticipantCreated(Long contestId, Long accountId) {
        log.info("👤 Publishing participant created event for account {} in contest {}", accountId, contestId);
        
        try {
            // Validate contest exists and is open for joining
            Contest contest = contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found with ID: " + contestId));
            
            if (!"OPEN".equals(contest.getStatus().toString())) {
                throw new IllegalStateException("Cannot join contest " + contestId + 
                    ". Contest is not open for joining. Current state: " + contest.getStatus());
            }
            
            // Create participant
            Participant participant = participantService.createParticipant(contestId, accountId);
            
            // Initialize participant in leaderboard with 0 score
            leaderBoardService.updateScore(contestId, participant.getId(), 0);
            
            GameEventDTO.ParticipantCreatedEvent event = GameEventDTO.ParticipantCreatedEvent.builder()
                    .eventType("participant.created")
                    .contestId(contestId)
                    .participantId(participant.getId())
                    .name(participant.getAccount().getFirstName())
                    .timestamp(Instant.now())
                    .build();
            
            log.info("✅ Participant {} created for contest {} with name: {}", 
                participant.getId(), contestId, participant.getAccount().getFirstName());
            
            return publishParticipantCreated(contestId, event);
            
        } catch (Exception e) {
            log.error("❌ Error creating participant for account {} in contest {}: {}", 
                accountId, contestId, e.getMessage(), e);
            throw new RuntimeException("Failed to create participant: " + e.getMessage(), e);
        }
    }
    public CompletableFuture<SendResult<String, Object>> publishLeaderboardRefresh(Long contestId) {
        log.info("🔄 Publishing leaderboard refresh event for contest {}", contestId);
        
        try {
            // Validate contest exists
            contestRepository.findById(contestId)
                    .orElseThrow(() -> new RuntimeException("Contest not found with ID: " + contestId));
            
            // Get current leaderboard
            List<LeaderBoardDTO.LeaderBoardResponse> leaderboard = leaderBoardService.getLeaderboard(contestId);
            
            GameEventDTO.LeaderboardRefreshEvent event = GameEventDTO.LeaderboardRefreshEvent.builder()
                    .eventType("leaderboard.refresh")
                    .contestId(contestId)
                    .timestamp(Instant.now())
                    .build();
            
            log.info("✅ Leaderboard refresh triggered for contest {} with {} participants", 
                contestId, leaderboard.size());
            
            return publishLeaderboardRefresh(contestId, event);
            
        } catch (Exception e) {
            log.error("❌ Error refreshing leaderboard for contest {}: {}", contestId, e.getMessage(), e);
            throw new RuntimeException("Failed to refresh leaderboard: " + e.getMessage(), e);
        }
    }


    public static QuestionDTO.QuestionResponse toResponse(Question question) {
        return new QuestionDTO.QuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getOptions().stream()
                        .map(EventProducer::answerToResponse)
                        .toList(),
                question.getPoints(),
                question.getTimeLimit()
        );
    }
    public static AnswerOptionDTO.AnswerOptionResponse answerToResponse(AnswerOption entity) {
        return new AnswerOptionDTO.AnswerOptionResponse(
                entity.getId(),
                entity.getOptionText(),
                entity.getIsCorrect(),
                entity.getQuestion() != null ? entity.getQuestion().getId() : null
        );
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Initialize leaderboard for all existing participants in a contest
     */
    private void initializeLeaderboardForContest(Long contestId) {
        try {
            leaderBoardService.initializeLeaderboard(contestId);
            log.info("✅ Leaderboard initialized for contest {}", contestId);
        } catch (Exception e) {
            log.error("❌ Error initializing leaderboard for contest {}: {}", contestId, e.getMessage(), e);
            throw new RuntimeException("Failed to initialize leaderboard for contest " + contestId, e);
        }
    }
    
    /**
     * Publish final leaderboard event
     */
    private void publishFinalLeaderboard(Long contestId, List<LeaderBoardDTO.LeaderBoardResponse> finalLeaderboard) {
        try {
            GameEventDTO.LeaderboardUpdatedEvent event = GameEventDTO.LeaderboardUpdatedEvent.builder()
                    .eventType("leaderboard.final")
                    .contestId(contestId)
                    .leaderboard(finalLeaderboard)
                    .timestamp(Instant.now())
                    .build();
            
            sendEvent(contestId, event);
            log.info("✅ Final leaderboard published for contest {} with {} participants", 
                contestId, finalLeaderboard.size());
                
        } catch (Exception e) {
            log.error("❌ Error publishing final leaderboard for contest {}: {}", contestId, e.getMessage(), e);
        }
    }
    
    /**
     * Publish score updated event internally
     */
    private void publishScoreUpdatedInternal(Long contestId, Long participantId, ScoreCalculationResult scoreResult) {
        try {
            // Get current total score
            List<LeaderBoardDTO.LeaderBoardResponse> leaderboard = leaderBoardService.getLeaderboard(contestId);
            int currentScore = leaderboard.stream()
                .filter(lb -> lb.participantId().equals(participantId))
                .mapToInt(LeaderBoardDTO.LeaderBoardResponse::score)
                .findFirst()
                .orElse(0);
            
            publishScoreUpdated(contestId, participantId, currentScore);
            
        } catch (Exception e) {
            log.error("❌ Error publishing score update for participant {} in contest {}: {}", 
                participantId, contestId, e.getMessage(), e);
        }
    }
    
    /**
     * Calculate score for an answer
     */
    private ScoreCalculationResult calculateAnswerScore(Long contestQuestionId, String answer) {
        try {
            // Get contest question
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                return new ScoreCalculationResult(false, 0, 0);
            }
            
            // Get question details
            var question = questionService.findById(contestQuestionOpt.get().questionId());
            if (question == null) {
                return new ScoreCalculationResult(false, 0, 0);
            }
            
            // Check if answer is correct by finding the correct option
            Long answerOptionId = parseAnswerOptionId(answer);
            boolean isCorrect = checkAnswerCorrectness(question, answerOptionId);
            
            // Calculate score based on correctness and question points
            int baseScore = isCorrect ? question.points() : 0;
            int timeBonus = calculateTimeBonus(3000); // Default 3 seconds
            int totalScore = baseScore + timeBonus;
            
            return new ScoreCalculationResult(isCorrect, totalScore, 3000);
            
        } catch (Exception e) {
            log.error("❌ Error calculating score for contest question {}: {}", contestQuestionId, e.getMessage(), e);
            return new ScoreCalculationResult(false, 0, 0);
        }
    }
    
    /**
     * Check if the selected answer option is correct
     */
    private boolean checkAnswerCorrectness(QuestionDTO.QuestionResponse question, Long answerOptionId) {
        return question.options().stream()
            .anyMatch(option -> option.id().equals(answerOptionId) && option.isCorrect());
    }
    
    /**
     * Calculate time bonus based on answer speed
     */
    private int calculateTimeBonus(int answerTimeMs) {
        // Simple time bonus: faster answers get more points
        // Max bonus: 100 points, decreases with time
        int maxBonus = 1000;
        int maxTime = 30000; // 30 seconds
        
        if (answerTimeMs >= maxTime) return 0;
        
        return Math.max(0, maxBonus - (answerTimeMs * maxBonus / maxTime));
    }
    
    /**
     * Parse answer string to get answer option ID
     */
    private Long parseAnswerOptionId(String answer) {
        try {
            return Long.parseLong(answer);
        } catch (NumberFormatException e) {
            // If answer is not a number, generate hash-based ID
            return (long) Math.abs(answer.hashCode());
        }
    }
    
    /**
     * Generate unique submission UUID
     */
    private Long generateSubmissionUuid(Long participantId, Long contestQuestionId, Long timestamp) {
        String uuidString = participantId + "_" + contestQuestionId + "_" + timestamp;
        return (long) Math.abs(uuidString.hashCode());
    }
    
    /**
     * Inner class for score calculation result
     */
    private static class ScoreCalculationResult {
        private final boolean correct;
        private final int score;
        private final int answerTime;
        
        public ScoreCalculationResult(boolean correct, int score, int answerTime) {
            this.correct = correct;
            this.score = score;
            this.answerTime = answerTime;
        }
        
        public boolean isCorrect() { return correct; }
        public int getScore() { return score; }
        public int getAnswerTime() { return answerTime; }
    }
}
