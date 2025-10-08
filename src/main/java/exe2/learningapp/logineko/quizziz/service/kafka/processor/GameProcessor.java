package exe2.learningapp.logineko.quizziz.service.kafka.processor;

import com.fasterxml.jackson.databind.JsonNode;
import exe2.learningapp.logineko.quizziz.service.ContestQuestionService;
import exe2.learningapp.logineko.quizziz.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameProcessor {
    
    private final ContestQuestionService contestQuestionService;
    private final QuestionService questionService;

    public void handleQuestionRevealed(Object event, Long contestId) {
        log.info("📝 Processing question revealed event for contest {}", contestId);
        
        try {
            JsonNode jsonEvent = (JsonNode) event;
            
            if (jsonEvent.has("contestQuestionId")) {
                Long contestQuestionId = jsonEvent.get("contestQuestionId").asLong();
                
                // Get contest question details for logging
                var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
                if (contestQuestionOpt.isPresent()) {
                    var contestQuestion = contestQuestionOpt.get();
                    
                    // Get question details for logging
                    var question = questionService.findById(contestQuestion.questionId());
                    if (question != null) {
                        log.info("✅ Question {} revealed for contest {} at index {} - Question: {}", 
                            contestQuestionId, contestId, contestQuestion.index(), question.questionText());
                    } else {
                        log.warn("⚠️ Question details not found for contest question ID: {}", contestQuestionId);
                    }
                } else {
                    log.warn("⚠️ Contest question not found for ID: {}", contestQuestionId);
                }
            }
        } catch (Exception e) {
            log.error("❌ Error processing question revealed event: {}", e.getMessage(), e);
        }
    }

    public void handleAnswerSubmitted(Object event, Long contestId) {
        log.info("📤 Processing answer submitted event for contest {}", contestId);
        
        try {
            JsonNode jsonEvent = (JsonNode) event;
            
            if (jsonEvent.has("participantId") && jsonEvent.has("contestQuestionId") && jsonEvent.has("answer")) {
                Long participantId = jsonEvent.get("participantId").asLong();
                Long contestQuestionId = jsonEvent.get("contestQuestionId").asLong();
                String answer = jsonEvent.get("answer").asText();
                
                log.info("✅ Answer processed for participant {} on question {} in contest {} - Answer: {}", 
                    participantId, contestQuestionId, contestId, answer);
                
                // Note: All business logic (validation, scoring, saving) is now handled in EventProducer
                // This processor just logs the event for monitoring and debugging
            }
        } catch (Exception e) {
            log.error("❌ Error processing answer submitted event: {}", e.getMessage(), e);
        }
    }
}