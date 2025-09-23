package exe2.learningapp.logineko.quizziz.controller;
import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.service.ContestService;
import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class TestController {

    private final SimpMessagingTemplate messagingTemplate;

    private final EventProducer producer;
    private final ContestService contestService;

    @PostMapping("/test-kafka/{contestId}")
    public ResponseEntity<String> testKafka(@PathVariable Long contestId) {
        GameEventDTO.ContestLifecycleEvent ev =
                new GameEventDTO.ContestLifecycleEvent("contest.started", contestId, null, Instant.now());
        producer.publishContestLifecycle(contestId, ev);
        contestService.startContest(contestId);
        return ResponseEntity.ok("Contest started");
    }
}