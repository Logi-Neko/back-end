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
    public CompletableFuture<SendResult<String, Object>> publishAnswerSubmitted(Long contestId, Long participantId, Long contestQuestionId, String answer, Integer timeSpent) {
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
            
            // Save answer to database WITHOUT calculating score yet
            // Score will be calculated when question time expires
            answerService.saveFromEvent(
                submissionUuid,
                participantId,
                contestQuestionId,
                findAnswerOptionId(contestQuestionId, answer.trim()),
                false, // Will be updated later when scoring
                0, // Score will be calculated later
                timeSpent != null ? timeSpent * 1000 : 0 // Convert to milliseconds
            );
            
            // Don't update leaderboard yet - wait for question to end
            
            // Create and publish answer submitted event
            GameEventDTO.AnswerSubmittedEvent event = GameEventDTO.AnswerSubmittedEvent.builder()
                    .eventType("answer.submitted")
                    .contestId(contestId)
                    .participantId(participantId)
                    .contestQuestionId(contestQuestionId)
                    .answer(answer.trim())
                    .timestamp(Instant.now())
                    .build();
            
            // Don't publish score updated event yet - wait for question to end
            
            log.info("✅ Answer submitted and saved for participant {} in contest {} - Score will be calculated when question ends", 
                participantId, contestId);
            
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

    /**
     * Calculate and update scores for a specific question when time expires
     */
    @Transactional
    public CompletableFuture<SendResult<String, Object>> publishQuestionEnded(Long contestQuestionId) {
        log.info("⏰ Publishing question ended event for contest question {}", contestQuestionId);
        
        try {
            // Get contest question details
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                throw new RuntimeException("Contest question not found with ID: " + contestQuestionId);
            }
            
            var contestQuestion = contestQuestionOpt.get();
            Long contestId = contestQuestion.contestId();
            
            // Get question details to find correct answer
            var question = questionService.findById(contestQuestion.questionId());
            if (question == null) {
                throw new RuntimeException("Question not found with ID: " + contestQuestion.questionId());
            }
            
            // Find correct answer
            String correctAnswer = question.options().stream()
                .filter(option -> option.isCorrect())
                .map(option -> option.optionText())
                .findFirst()
                .orElse("");
            
            // Calculate scores for all submitted answers
            calculateAndUpdateScoresForQuestion(contestQuestionId, correctAnswer);
            
            // Create question ended event with correct answer
            GameEventDTO.QuestionEndedEvent event = GameEventDTO.QuestionEndedEvent.builder()
                    .eventType("question.ended")
                    .contestId(contestId)
                    .contestQuestionId(contestQuestionId)
                    .correctAnswer(correctAnswer)
                    .timestamp(Instant.now())
                    .build();
            
            log.info("✅ Question {} ended for contest {} - Correct answer: {}", 
                contestQuestionId, contestId, correctAnswer);
            
            return sendEvent(contestId, event);
            
        } catch (Exception e) {
            log.error("❌ Error ending question {}: {}", contestQuestionId, e.getMessage(), e);
            throw new RuntimeException("Failed to end question: " + e.getMessage(), e);
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
     * Calculate time bonus based on answer speed
     */
    private int calculateScoreByTime(int answerTimeMs, int questionTimeLimitSec, int questionPoints) {
        double questionTimeMs = questionTimeLimitSec * 1000.0;

        if (answerTimeMs >= questionTimeMs || questionTimeMs <= 0) {
            return 0;
        }

        double score = questionPoints * (1.0 - (answerTimeMs / questionTimeMs));

        return Math.max(0, (int) Math.round(score));
    }
    
    /**
     * Find answer option ID by matching answer text
     */
    private Long findAnswerOptionId(Long contestQuestionId, String answer) {
        try {
            // Get contest question
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                return 0L;
            }
            
            // Get question details
            var question = questionService.findById(contestQuestionOpt.get().questionId());
            if (question == null) {
                return 0L;
            }
            
            // Find matching option
            return question.options().stream()
                .filter(option -> option.optionText().equals(answer))
                .map(option -> option.id())
                .findFirst()
                .orElse(0L);
                
        } catch (Exception e) {
            log.error("❌ Error finding answer option ID for answer '{}': {}", answer, e.getMessage());
            return 0L;
        }
    }
    
    /**
     * Calculate and update scores for all answers of a specific question
     */
    private void calculateAndUpdateScoresForQuestion(Long contestQuestionId, String correctAnswer) {
        try {
            // Get all answers for this question (using Pageable.unpaged() to get all)
            var answersPage = answerService.findByContestQuestion(contestQuestionId, org.springframework.data.domain.Pageable.unpaged());
            var answers = answersPage.getContent();
            
            // Get contest question details
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                log.error("❌ Contest question not found for ID: {}", contestQuestionId);
                return;
            }
            
            var contestQuestion = contestQuestionOpt.get();
            Long contestId = contestQuestion.contestId();
            
            // Get question details
            var question = questionService.findById(contestQuestion.questionId());
            if (question == null) {
                log.error("❌ Question not found for ID: {}", contestQuestion.questionId());
                return;
            }
            
            log.info("📊 Calculating scores for question {} - Correct answer: {}", contestQuestionId, correctAnswer);
            
            for (var answer : answers) {
                try {
                    // Get participant's answer text
                    String participantAnswer = getAnswerTextFromOptionId(answer.answerOptionId(), question);

                    // Calculate score
                    boolean isCorrect = correctAnswer.equals(participantAnswer);
                    int totalScore;
                    if (isCorrect) {
                        // NẾU ĐÚNG: Tính điểm dựa trên thời gian
                        totalScore = calculateScoreByTime(answer.answerTime(), question.timeLimit(), question.points());
                    } else {
                        // NẾU SAI: Điểm là 0
                        totalScore = 0;
                    }
                    // Update answer with correct score
                    answerService.updateAnswerScore(answer.id(), isCorrect, totalScore);
                    
                    // Update leaderboard with delta score (not total score)
                    leaderBoardService.updateScore(contestId, answer.participantId(), totalScore);
                    
                    log.info("✅ Updated score for participant {}: {} points (Correct: {})", 
                        answer.participantId(), totalScore, isCorrect);
                        
                } catch (Exception e) {
                    log.error("❌ Error calculating score for participant {}: {}", 
                        answer.participantId(), e.getMessage());
                }
            }
            
            // Send leaderboard update after all scores are calculated
            log.info("📊 Sending leaderboard update for contest {}", contestId);
            publishLeaderboardRefresh(contestId);
            
        } catch (Exception e) {
            log.error("❌ Error calculating scores for question {}: {}", contestQuestionId, e.getMessage(), e);
        }
    }
    
    /**
     * Get answer text from option ID
     */
    private String getAnswerTextFromOptionId(Long optionId, QuestionDTO.QuestionResponse question) {
        return question.options().stream()
            .filter(option -> option.id().equals(optionId))
            .map(option -> option.optionText())
            .findFirst()
            .orElse("");
    }

    /**
     * Generate unique submission UUID
     */
    private Long generateSubmissionUuid(Long participantId, Long contestQuestionId, Long timestamp) {
        String uuidString = participantId + "_" + contestQuestionId + "_" + timestamp;
        return (long) Math.abs(uuidString.hashCode());
    }
    
}
