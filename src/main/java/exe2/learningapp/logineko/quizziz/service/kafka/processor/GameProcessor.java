package exe2.learningapp.logineko.quizziz.service.kafka.processor;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.AnswerService;
import exe2.learningapp.logineko.quizziz.service.ContestQuestionService;
import exe2.learningapp.logineko.quizziz.service.QuestionService;
import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameProcessor {
    
    private final ContestQuestionService contestQuestionService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final EventProducer eventProducer;

    public void handleQuestionRevealed(Object event, Long contestId) {
        log.info("Processing question revealed for contest {}", contestId);
        
        try {
            if (event instanceof GameEventDTO.QuestionRevealedEvent) {
                GameEventDTO.QuestionRevealedEvent questionEvent = (GameEventDTO.QuestionRevealedEvent) event;
                
                // Get contest question details
                var contestQuestion = contestQuestionService.findById(questionEvent.getContestQuestionId());
                if (contestQuestion.isEmpty()) {
                    log.warn("Contest question not found for ID: {}", questionEvent.getContestQuestionId());
                    return;
                }
                
                // Get question details
                var question = questionService.findById(contestQuestion.get().questionId());
                if (question != null) {
                    // Update the event with actual question data
                    questionEvent.setQuestion(question);
                    questionEvent.setOrderIndex(contestQuestion.get().index());
                }
                
                log.info("Question {} revealed for contest {} at index {}", 
                    questionEvent.getContestQuestionId(), contestId, questionEvent.getOrderIndex());
            }
        } catch (Exception e) {
            log.error("Error processing question revealed event: {}", e.getMessage(), e);
        }
    }

    public void handleAnswerSubmitted(Object event, Long contestId) {
        log.info("Processing answer submitted for contest {}", contestId);
        
        try {
            if (event instanceof GameEventDTO.AnswerSubmittedEvent) {
                GameEventDTO.AnswerSubmittedEvent answerEvent = (GameEventDTO.AnswerSubmittedEvent) event;
                
                // Check for duplicate submission using submission UUID
                Long submissionUuid = generateSubmissionUuid(answerEvent);
                if (answerService.existsBySubmissionUuid(submissionUuid)) {
                    log.warn("Duplicate answer submission detected for UUID: {}", submissionUuid);
                    return;
                }
                
                // Calculate score first
                ScoreCalculationResult scoreResult = calculateScore(answerEvent);
                
                // Save answer to database with score information
                answerService.saveFromEvent(
                    submissionUuid,
                    answerEvent.getParticipantId(),
                    answerEvent.getContestQuestionId(),
                    parseAnswerOptionId(answerEvent.getAnswer()),
                    scoreResult.isCorrect(),
                    scoreResult.getScore(),
                    calculateAnswerTime(answerEvent)
                );
                
                // Publish score update event
                publishScoreUpdate(answerEvent, scoreResult);
                
                log.info("Answer submitted by participant {} for question {} in contest {} - Score: {} (Correct: {})", 
                    answerEvent.getParticipantId(), answerEvent.getContestQuestionId(), contestId, 
                    scoreResult.getScore(), scoreResult.isCorrect());
            }
        } catch (Exception e) {
            log.error("Error processing answer submitted event: {}", e.getMessage(), e);
        }
    }

    private ScoreCalculationResult calculateScore(GameEventDTO.AnswerSubmittedEvent answerEvent) {
        try {
            // Get contest question to find the actual question
            var contestQuestion = contestQuestionService.findById(answerEvent.getContestQuestionId());
            if (contestQuestion.isEmpty()) {
                log.warn("Contest question not found for ID: {}", answerEvent.getContestQuestionId());
                return new ScoreCalculationResult(false, 0);
            }
            
            // Get question details
            var question = questionService.findById(contestQuestion.get().questionId());
            if (question == null) {
                log.warn("Question not found for ID: {}", contestQuestion.get().questionId());
                return new ScoreCalculationResult(false, 0);
            }
            
            // Check if answer is correct
            boolean isCorrect = checkAnswerCorrectness(question, answerEvent.getAnswer());
            
            // Calculate score based on correctness and time taken
            int score = calculateScorePoints(isCorrect, calculateAnswerTime(answerEvent));
            
            return new ScoreCalculationResult(isCorrect, score);
            
        } catch (Exception e) {
            log.error("Error calculating score: {}", e.getMessage(), e);
            return new ScoreCalculationResult(false, 0);
        }
    }

    private boolean checkAnswerCorrectness(Object question, String submittedAnswer) {
        try {
            // This is a simplified implementation
            // In a real application, you would compare with the correct answer from the question object
            // For now, we'll use a simple check - non-empty answers are considered correct
            return submittedAnswer != null && !submittedAnswer.trim().isEmpty();
        } catch (Exception e) {
            log.error("Error checking answer correctness: {}", e.getMessage(), e);
            return false;
        }
    }

    private int calculateScorePoints(boolean isCorrect, int answerTimeMs) {
        if (!isCorrect) {
            return 0;
        }
        
        // Base score for correct answer
        int baseScore = 10;
        
        // Bonus for quick answers (under 5 seconds)
        if (answerTimeMs < 5000) {
            baseScore += 5;
        }
        
        return baseScore;
    }

    private void publishScoreUpdate(GameEventDTO.AnswerSubmittedEvent answerEvent, ScoreCalculationResult scoreResult) {
        try {
            // Get current participant score from leaderboard
            int currentScore = getCurrentParticipantScore(answerEvent.getContestId(), answerEvent.getParticipantId());
            int newScore = currentScore + scoreResult.getScore();
            
            // Publish score update event
            GameEventDTO.ScoreUpdatedEvent scoreEvent = GameEventDTO.ScoreUpdatedEvent.builder()
                    .eventType("score.updated")
                    .contestId(answerEvent.getContestId())
                    .participantId(answerEvent.getParticipantId())
                    .score(newScore)
                    .timestamp(Instant.now())
                    .build();
            
            eventProducer.publishScoreUpdated(answerEvent.getContestId(), scoreEvent);
            
            log.info("Score updated for participant {} in contest {}: {} (+{})", 
                answerEvent.getParticipantId(), answerEvent.getContestId(), newScore, scoreResult.getScore());
                
        } catch (Exception e) {
            log.error("Error publishing score update: {}", e.getMessage(), e);
        }
    }

    private int getCurrentParticipantScore(Long contestId, Long participantId) {
        try {
            // Get current score from leaderboard service
            // This would typically come from LeaderBoardService
            // For now, return 0 as starting score
            return 0;
        } catch (Exception e) {
            log.error("Error getting current participant score: {}", e.getMessage(), e);
            return 0;
        }
    }

    private Long generateSubmissionUuid(GameEventDTO.AnswerSubmittedEvent answerEvent) {
        // Generate a unique submission UUID based on participant, question, and timestamp
        String uuidString = answerEvent.getParticipantId() + "_" + 
                           answerEvent.getContestQuestionId() + "_" + 
                           answerEvent.getTimestamp().toEpochMilli();
        return (long) Math.abs(uuidString.hashCode());
    }

    private Long parseAnswerOptionId(String answer) {
        try {
            // Try to parse as Long if it's a numeric answer option ID
            return Long.parseLong(answer);
        } catch (NumberFormatException e) {
            // If not numeric, generate a hash-based ID
            return (long) Math.abs(answer.hashCode());
        }
    }

    private int calculateAnswerTime(GameEventDTO.AnswerSubmittedEvent answerEvent) {
        // Calculate time taken to answer (simplified)
        // In a real app, this would be calculated from question reveal time
        return 3000; // Default 3 seconds
    }

    // Inner class for score calculation result
    private static class ScoreCalculationResult {
        private final boolean correct;
        private final int score;

        public ScoreCalculationResult(boolean correct, int score) {
            this.correct = correct;
            this.score = score;
        }

        public boolean isCorrect() { return correct; }
        public int getScore() { return score; }
    }
}

