 package exe2.learningapp.logineko.quizziz.controller;



import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.service.ContestService;
import exe2.learningapp.logineko.quizziz.service.ContestQuestionService;
import exe2.learningapp.logineko.quizziz.service.QuestionService;
import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final EventProducer producer;
    private final ContestService contestService;
    private final ContestQuestionService contestQuestionService;
    private final QuestionService questionService;
    // Host starts contest -> publish contest.started
    @PostMapping("/{contestId}/start")
    public ResponseEntity<String> startContest(@Valid  @PathVariable Long contestId) {
        GameEventDTO.ContestLifecycleEvent ev =
                new GameEventDTO.ContestLifecycleEvent("contest.started", contestId, null, Instant.now());
        producer.publishContestLifecycle(contestId, ev);
        contestService.startContest(contestId);
        return ResponseEntity.ok("Contest started");
    }

    @PostMapping("/{contestId}/reveal/{contestQuestionId}")
    public ResponseEntity<String> revealQuestion(@PathVariable Long contestId, @PathVariable Long contestQuestionId) {
        log.info("Revealing question {} for contest {}", contestQuestionId, contestId);
        
        // Get contest question details
        var contestQuestion = contestQuestionService.findById(contestQuestionId)
                .orElseThrow(() -> new RuntimeException("Contest question not found"));
        
        // Get question details
        var question = questionService.findById(contestQuestion.questionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Create question revealed event
        GameEventDTO.QuestionRevealedEvent ev = new GameEventDTO.QuestionRevealedEvent(
                "question.revealed",
                contestId,
                contestQuestionId,
                contestQuestion.index(),
                question,
                Instant.now()
        );
        
        producer.publishQuestionRevealed(contestId, ev);
        return ResponseEntity.ok("Question revealed successfully");
    }

    @PostMapping("/{contestId}/submit")
    public void submitAnswer(@PathVariable Long contestId, @RequestBody GameEventDTO.AnswerSubmittedEvent request) {
        // request should contain submissionUuid for idempotency
        producer.publishAnswerSubmitted(contestId, request);
    }

    // End contest
    @PostMapping("/{contestId}/end")
    public ResponseEntity<String> endContest(@PathVariable Long contestId) {
        log.info("Ending contest {}", contestId);
        GameEventDTO.ContestLifecycleEvent ev = new GameEventDTO.ContestLifecycleEvent("contest.ended", contestId, null, Instant.now());
        producer.publishContestLifecycle(contestId, ev);
        contestService.endContest(contestId);
        return ResponseEntity.ok("Contest ended successfully");
    }

    // Participant joins contest
    @PostMapping("/{contestId}/join/{participantId}")
    public ResponseEntity<String> joinContest(@PathVariable Long contestId, @PathVariable Long participantId) {
        log.info("Participant {} joining contest {}", participantId, contestId);
        GameEventDTO.ContestLifecycleEvent ev = new GameEventDTO.ContestLifecycleEvent("participant.joined", contestId, participantId, Instant.now());
        producer.publishContestLifecycle(contestId, ev);
        return ResponseEntity.ok("Participant joined successfully");
    }

    // Participant leaves contest
    @PostMapping("/{contestId}/leave/{participantId}")
    public ResponseEntity<String> leaveContest(@PathVariable Long contestId, @PathVariable Long participantId) {
        log.info("Participant {} leaving contest {}", participantId, contestId);
        GameEventDTO.ContestLifecycleEvent ev = new GameEventDTO.ContestLifecycleEvent("participant.left", contestId, participantId, Instant.now());
        producer.publishContestLifecycle(contestId, ev);
        return ResponseEntity.ok("Participant left successfully");
    }

    // Get contest leaderboard
    @GetMapping("/{contestId}/leaderboard")
    public ResponseEntity<Object> getLeaderboard(@PathVariable Long contestId) {
        log.info("Getting leaderboard for contest {}", contestId);
        // This would typically return leaderboard data
        // For now, just return a placeholder
        return ResponseEntity.ok("Leaderboard data for contest " + contestId);
    }
}
