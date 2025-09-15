 package exe2.learningapp.logineko.quizziz.controller;



import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

 @RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final EventProducer producer;

    // Host starts contest -> publish contest.started
    @PostMapping("/{contestId}/start")
    public void startContest(@PathVariable Long contestId) {
        GameEventDTO.ContestLifecycleEvent ev = new GameEventDTO.ContestLifecycleEvent("contest.started", contestId, null, Instant.now());
        producer.publishContestLifecycle(contestId, ev);
    }

    @PostMapping("/{contestId}/reveal/{contestQuestionId}")
    public void revealQuestion(@PathVariable Long contestId, @PathVariable Long contestQuestionId ) {

       // GameEventDTO.QuestionRevealedEvent ev = new GameEventDTO.QuestionRevealedEvent("question.revealed", contestId, contestQuestionId, 1, qp, Instant.now());
       // producer.publishQuestionRevealed(contestId, ev);
    }

    @PostMapping("/{contestId}/submit")
    public void submitAnswer(@PathVariable Long contestId, @RequestBody GameEventDTO.AnswerSubmittedEvent request) {
        // request should contain submissionUuid for idempotency
        producer.publishAnswerSubmitted(contestId, request);
    }

    // End contest
    @PostMapping("/{contestId}/end")
    public void endContest(@PathVariable Long contestId) {
        GameEventDTO.ContestLifecycleEvent ev = new GameEventDTO.ContestLifecycleEvent("contest.ended", contestId, null, Instant.now());
        producer.publishContestLifecycle(contestId, ev);
    }
}
