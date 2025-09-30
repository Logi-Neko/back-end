     package exe2.learningapp.logineko.quizziz.controller;



    import exe2.learningapp.logineko.quizziz.dto.ContestDTO;
    import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
    import exe2.learningapp.logineko.quizziz.service.ContestService;
    import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import java.time.Instant;

    @RestController
    @RequestMapping("/api/game")
    @RequiredArgsConstructor
    @Slf4j
    @Tag(name = "Game Management", description = "API quản lý game và contest cho người chơi")
    public class GameController {
        private final EventProducer eventProducer;
        private final ContestService contestService;

        @PostMapping("/create")
        @Operation(summary = "Tạo contest mới", description = "Tạo một contest mới với ID được chỉ định")
        public ResponseEntity<?> createContest(@RequestBody ContestDTO.ContestRequest request) {
            log.info("Creating contest with title: {}", request.title());
            ContestDTO.ContestResponse contest = contestService.create(request);

            eventProducer.publishContestCreated(contest.id());
            return ResponseEntity.ok(contest);
        }

        @PostMapping("/{contestId}/start")
        @Operation(summary = "Bắt đầu contest", description = "Bắt đầu contest với ID được chỉ định")
        public ResponseEntity<Void> startContest(@PathVariable Long contestId) {
            log.info("Starting contest with ID: {}", contestId);
            eventProducer.publishContestStarted(contestId);
            return ResponseEntity.ok().build();
        }

        @PostMapping("/reveal/{contestQuestionId}")
        @Operation(summary = "Hiển thị câu hỏi", description = "Hiển thị câu hỏi cho contest")
        public ResponseEntity<Void> revealQuestion( @PathVariable Long contestQuestionId) {
            log.info("Revealing question {} for contest {}", contestQuestionId);
            eventProducer.publishQuestionRevealed(contestQuestionId);
            return ResponseEntity.ok().build();
        }

        @PostMapping("/{contestId}/submit/{participantId}/{contestQuestionId}")
        @Operation(summary = "Nộp câu trả lời", description = "Nộp câu trả lời cho câu hỏi")
        public ResponseEntity<Void> submitAnswer(@PathVariable Long contestId, @PathVariable Long participantId,
                                                 @PathVariable Long contestQuestionId, @RequestParam String answer) {
            log.info("Submitting answer for participant {} in contest {}", participantId, contestId);
            eventProducer.publishAnswerSubmitted(contestId, participantId, contestQuestionId, answer);
            return ResponseEntity.ok().build();
        }

        @PostMapping("/{contestId}/join")
        @Operation(summary = "Tham gia contest", description = "Tham gia contest với tên người chơi")
        public ResponseEntity<Void> joinContest(@PathVariable Long contestId, @RequestParam Long accountId) {
            log.info("Participant {} joining contest {} with name: {}", accountId, contestId);
            eventProducer.publishParticipantCreated(contestId, accountId);
            return ResponseEntity.ok().build();
        }

        @PostMapping("/{contestId}/end")
        @Operation(summary = "Kết thúc contest", description = "Kết thúc contest với ID được chỉ định")
        public ResponseEntity<Void> endContest(@PathVariable Long contestId) {
            log.info("Ending contest with ID: {}", contestId);
            eventProducer.publishContestEnded(contestId);
            return ResponseEntity.ok().build();
        }

        @PostMapping("/{contestId}/leaderboard/refresh")
        @Operation(summary = "Làm mới bảng xếp hạng", description = "Kích hoạt làm mới bảng xếp hạng real-time")
        public ResponseEntity<Void> refreshLeaderboard(@PathVariable Long contestId) {
            log.info("Refreshing leaderboard for contest: {}", contestId);
            eventProducer.publishLeaderboardRefresh(contestId);
            return ResponseEntity.ok().build();
        }
    }