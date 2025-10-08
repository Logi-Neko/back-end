 package exe2.learningapp.logineko.quizziz.controller;

import exe2.learningapp.logineko.quizziz.dto.ContestDTO;
import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import exe2.learningapp.logineko.quizziz.service.ContestService;
import exe2.learningapp.logineko.quizziz.service.ContestQuestionService;
import exe2.learningapp.logineko.quizziz.service.LeaderBoardService;
import exe2.learningapp.logineko.quizziz.service.ParticipantService;
import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
import exe2.learningapp.logineko.quizziz.service.kafka.processor.ContestStateManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Game Management", description = "API quản lý game và contest online real-time")
public class GameController {

    private final EventProducer eventProducer;
    private final ContestService contestService;
    private final ContestQuestionService contestQuestionService;
    private final ParticipantService participantService;
    private final LeaderBoardService leaderBoardService;
    private final ContestStateManager contestStateManager;

    // ==================== CONTEST LIFECYCLE ====================
    
    @PostMapping("/create")
    @Operation(summary = "Tạo contest mới", description = "Tạo contest mới và publish event để khởi tạo hệ thống")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tạo contest thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> createContest(@Valid @RequestBody ContestDTO.ContestRequest request) {
        log.info("🎯 Creating contest with title: {}", request.title());
        
        try {
            // Tạo contest trong database
            ContestDTO.ContestResponse contest = contestService.create(request);
            
            // Publish contest created event
            eventProducer.publishContestCreated(contest.id());
            
            log.info("✅ Contest created successfully with ID: {} and code: {}", contest.id(), contest.code());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Contest created successfully",
                "data", contest
            ));
            
        } catch (Exception e) {
            log.error("❌ Error creating contest: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error creating contest: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{contestId}/start")
    @Operation(summary = "Bắt đầu contest", description = "Bắt đầu contest và khởi tạo leaderboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bắt đầu contest thành công"),
        @ApiResponse(responseCode = "404", description = "Contest không tồn tại"),
        @ApiResponse(responseCode = "400", description = "Contest không thể bắt đầu"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> startContest(@PathVariable Long contestId) {
        log.info("🚀 Starting contest with ID: {}", contestId);
        
        try {
            // Validate contest exists and can be started
            var contestOpt = contestService.findById(contestId);
            if (contestOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest not found"));
            }
            
            var contest = contestOpt.get();
            
            // Check if contest is in correct state to start
            if (!"OPEN".equals(contest.status().toString())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Contest is not in OPEN state, current state: " + contest.status()));
            }
            
            // Check if contest has questions
            var questions = contestQuestionService.findByContest(contestId);
            if (questions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Contest has no questions"));
            }
            
            // Publish contest started event
            eventProducer.publishContestStarted(contestId);
            
            log.info("✅ Contest {} started successfully with {} questions", contestId, questions.size());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Contest started successfully",
                "data", Map.of(
                    "contestId", contestId,
                    "totalQuestions", questions.size(),
                    "status", "RUNNING"
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error starting contest {}: {}", contestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error starting contest: " + e.getMessage()));
        }
    }

    // ==================== QUESTION MANAGEMENT ====================
    
    @PostMapping("/reveal/{contestQuestionId}")
    @Operation(summary = "Hiển thị câu hỏi", description = "Hiển thị câu hỏi cho tất cả participants trong contest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hiển thị câu hỏi thành công"),
        @ApiResponse(responseCode = "404", description = "Câu hỏi không tồn tại"),
        @ApiResponse(responseCode = "400", description = "Contest chưa bắt đầu"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> revealQuestion(@PathVariable Long contestQuestionId) {
        log.info("📝 Revealing question {} for contest", contestQuestionId);
        
        try {
            // Validate contest question exists
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest question not found"));
            }
            
            var contestQuestion = contestQuestionOpt.get();
            Long contestId = contestQuestion.contestId();
            
            // Check if contest is running
            var contestState = contestStateManager.getContestState(contestId);
            if (contestState == null || !"RUNNING".equals(contestState.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Contest is not running"));
            }
            
            // Publish question revealed event
            eventProducer.publishQuestionRevealed(contestQuestionId);
            
            log.info("✅ Question {} revealed successfully for contest {}", contestQuestionId, contestId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Question revealed successfully",
                "data", Map.of(
                    "contestQuestionId", contestQuestionId,
                    "contestId", contestId,
                    "questionIndex", contestQuestion.index()
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error revealing question {}: {}", contestQuestionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error revealing question: " + e.getMessage()));
        }
    }

    @PostMapping("/end-question/{contestQuestionId}")
    @Operation(summary = "Kết thúc câu hỏi", description = "Kết thúc câu hỏi, hiển thị đáp án đúng và tính điểm")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kết thúc câu hỏi thành công"),
        @ApiResponse(responseCode = "404", description = "Câu hỏi không tồn tại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> endQuestion(@PathVariable Long contestQuestionId) {
        log.info("⏰ Ending question {}", contestQuestionId);
        
        try {
            // Validate contest question exists
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest question not found"));
            }
            
            var contestQuestion = contestQuestionOpt.get();
            Long contestId = contestQuestion.contestId();
            
            // Publish question ended event (this will calculate scores)
            eventProducer.publishQuestionEnded(contestQuestionId);
            
            log.info("✅ Question {} ended successfully for contest {}", contestQuestionId, contestId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Question ended successfully",
                "data", Map.of(
                    "contestQuestionId", contestQuestionId,
                    "contestId", contestId
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error ending question {}: {}", contestQuestionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error ending question: " + e.getMessage()));
        }
    }

    // ==================== ANSWER SUBMISSION ====================
    
    @PostMapping("/{contestId}/submit/{participantId}/{contestQuestionId}")
    @Operation(summary = "Nộp câu trả lời", description = "Nộp câu trả lời cho câu hỏi và tính điểm real-time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nộp câu trả lời thành công"),
        @ApiResponse(responseCode = "404", description = "Participant hoặc câu hỏi không tồn tại"),
        @ApiResponse(responseCode = "400", description = "Contest không đang chạy hoặc đã nộp rồi"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long contestId, 
            @PathVariable Long participantId,
            @PathVariable Long contestQuestionId, 
            @RequestParam String answer,
            @RequestParam(required = false) Integer timeSpent) {
        
        log.info("📤 Submitting answer for participant {} in contest {} for question {}", 
            participantId, contestId, contestQuestionId);
        
        try {
            // Validate contest is running
            var contestState = contestStateManager.getContestState(contestId);
            if (contestState == null || !"RUNNING".equals(contestState.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Contest is not running"));
            }
            
            // Validate participant exists
            var participantOpt = participantService.findById(participantId);
            if (participantOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Participant not found"));
            }
            
            // Validate contest question exists
            var contestQuestionOpt = contestQuestionService.findById(contestQuestionId);
            if (contestQuestionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest question not found"));
            }
            
            // Validate answer is not empty
            if (answer == null || answer.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Answer cannot be empty"));
            }
            
            // Publish answer submitted event with timeSpent (but don't calculate score yet)
            eventProducer.publishAnswerSubmitted(contestId, participantId, contestQuestionId, answer.trim(), timeSpent);
            
            log.info("✅ Answer submitted successfully for participant {} in contest {}", participantId, contestId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Answer submitted successfully",
                "data", Map.of(
                    "contestId", contestId,
                    "participantId", participantId,
                    "contestQuestionId", contestQuestionId,
                    "submittedAt", System.currentTimeMillis()
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error submitting answer for participant {} in contest {}: {}", 
                participantId, contestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error submitting answer: " + e.getMessage()));
        }
    }

    // ==================== PARTICIPANT MANAGEMENT ====================
    
    @PostMapping("/{contestId}/join")
    @Operation(summary = "Tham gia contest", description = "Tham gia contest và khởi tạo participant trong leaderboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tham gia contest thành công"),
        @ApiResponse(responseCode = "404", description = "Contest không tồn tại"),
        @ApiResponse(responseCode = "400", description = "Contest đã bắt đầu hoặc đã tham gia"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> joinContest(@PathVariable Long contestId, @RequestParam Long accountId) {
        log.info("👤 Account {} joining contest {}", accountId, contestId);
        
        try {
            // Validate contest exists
            var contestOpt = contestService.findById(contestId);
            if (contestOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest not found"));
            }
            
            var contest = contestOpt.get();
            
            // Check if contest is still open for joining
            if (!"OPEN".equals(contest.status().toString())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Contest is not open for joining, current state: " + contest.status()));
            }
            
            // Publish participant created event (EventProducer will handle participant creation)
            eventProducer.publishParticipantCreated(contestId, accountId);
            
            log.info("✅ Account {} joined contest {} successfully", accountId, contestId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Joined contest successfully",
                "data", Map.of(
                    "contestId", contestId,
                    "accountId", accountId,
                    "contestTitle", contest.title(),
                    "contestCode", contest.code()
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error joining contest {} for account {}: {}", contestId, accountId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error joining contest: " + e.getMessage()));
        }
    }

    @PostMapping("/{contestId}/end")
    @Operation(summary = "Kết thúc contest", description = "Kết thúc contest và finalize leaderboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kết thúc contest thành công"),
        @ApiResponse(responseCode = "404", description = "Contest không tồn tại"),
        @ApiResponse(responseCode = "400", description = "Contest chưa bắt đầu hoặc đã kết thúc"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> endContest(@PathVariable Long contestId) {
        log.info("🏁 Ending contest with ID: {}", contestId);
        
        try {
            // Validate contest exists
            var contestOpt = contestService.findById(contestId);
            if (contestOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest not found"));
            }
            
            // Check if contest is running
            var contestState = contestStateManager.getContestState(contestId);
            if (contestState == null || !"RUNNING".equals(contestState.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Contest is not running"));
            }
            
            // Publish contest ended event
            eventProducer.publishContestEnded(contestId);
            
            log.info("✅ Contest {} ended successfully", contestId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Contest ended successfully",
                "data", Map.of(
                    "contestId", contestId,
                    "endedAt", System.currentTimeMillis()
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error ending contest {}: {}", contestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error ending contest: " + e.getMessage()));
        }
    }

    // ==================== LEADERBOARD MANAGEMENT ====================
    
    @PostMapping("/{contestId}/leaderboard/refresh")
    @Operation(summary = "Làm mới bảng xếp hạng", description = "Kích hoạt làm mới bảng xếp hạng real-time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Làm mới leaderboard thành công"),
        @ApiResponse(responseCode = "404", description = "Contest không tồn tại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> refreshLeaderboard(@PathVariable Long contestId) {
        log.info("🔄 Refreshing leaderboard for contest: {}", contestId);
        
        try {
            // Validate contest exists
            var contestOpt = contestService.findById(contestId);
            if (contestOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest not found"));
            }
            
            // Publish leaderboard refresh event
            eventProducer.publishLeaderboardRefresh(contestId);
            
            log.info("✅ Leaderboard refresh triggered for contest {}", contestId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Leaderboard refresh triggered successfully",
                "data", Map.of(
                    "contestId", contestId,
                    "refreshedAt", System.currentTimeMillis()
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error refreshing leaderboard for contest {}: {}", contestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error refreshing leaderboard: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{contestId}/leaderboard")
    @Operation(summary = "Lấy bảng xếp hạng", description = "Lấy bảng xếp hạng hiện tại của contest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy leaderboard thành công"),
        @ApiResponse(responseCode = "404", description = "Contest không tồn tại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> getLeaderboard(@PathVariable Long contestId) {
        log.info("📊 Getting leaderboard for contest: {}", contestId);
        
        try {
            // Validate contest exists
            var contestOpt = contestService.findById(contestId);
            if (contestOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest not found"));
            }
            
            // Get leaderboard
            List<LeaderBoardDTO.LeaderBoardResponse> leaderboard = leaderBoardService.getLeaderboard(contestId);
            
            log.info("✅ Retrieved leaderboard for contest {} with {} participants", contestId, leaderboard.size());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Leaderboard retrieved successfully",
                "data", Map.of(
                    "contestId", contestId,
                    "leaderboard", leaderboard,
                    "totalParticipants", leaderboard.size()
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error getting leaderboard for contest {}: {}", contestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error getting leaderboard: " + e.getMessage()));
        }
    }
    
    // ==================== CONTEST STATUS ====================
    
    @GetMapping("/{contestId}/status")
    @Operation(summary = "Lấy trạng thái contest", description = "Lấy trạng thái hiện tại của contest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy trạng thái thành công"),
        @ApiResponse(responseCode = "404", description = "Contest không tồn tại"),
        @ApiResponse(responseCode = "500", description = "Lỗi server")
    })
    public ResponseEntity<?> getContestStatus(@PathVariable Long contestId) {
        log.info("📋 Getting status for contest: {}", contestId);
        
        try {
            // Validate contest exists
            var contestOpt = contestService.findById(contestId);
            if (contestOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Contest not found"));
            }
            
            var contest = contestOpt.get();
            var contestState = contestStateManager.getContestState(contestId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Contest status retrieved successfully",
                "data", Map.of(
                    "contestId", contestId,
                    "title", contest.title(),
                    "code", contest.code(),
                    "status", contest.status(),
                    "realTimeStatus", contestState != null ? contestState.getStatus() : "UNKNOWN",
                    "isActive", contestStateManager.isContestActive(contestId),
                    "isEnded", contestStateManager.isContestEnded(contestId)
                )
            ));
            
        } catch (Exception e) {
            log.error("❌ Error getting status for contest {}: {}", contestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error getting contest status: " + e.getMessage()));
        }
    }
}