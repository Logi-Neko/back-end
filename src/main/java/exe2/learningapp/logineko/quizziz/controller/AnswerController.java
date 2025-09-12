package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/contests/{contestId}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final EventProducer eventProducer;
    private final Random random = new Random();
    @PostMapping
    public void submitAnswer(
            @PathVariable Long contestId,
            @RequestParam Long participantId,
            @RequestParam Long contestQuestionId,
            @RequestParam Long answerOptionId,
            @RequestParam Long timeTakenMs
    ) {
        GameEventDTO.AnswerSubmittedEvent ev = new GameEventDTO.AnswerSubmittedEvent(
                "answer.submitted",
                contestId,
                participantId,
                contestQuestionId,
                answerOptionId,
                Instant.now(),// submissionUuid
                random.nextLong(),
                timeTakenMs
        );
        eventProducer.publishAnswerSubmitted(contestId, ev);
    }
}
