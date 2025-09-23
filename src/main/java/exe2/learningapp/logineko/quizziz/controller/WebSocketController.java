package exe2.learningapp.logineko.quizziz.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/contest.join")
    public void joinContest(@Payload Map<String, Object> payload) {
        log.info("WebSocket join contest request: {}", payload);
        Long contestId = Long.valueOf(payload.get("contestId").toString());
        Long participantId = Long.valueOf(payload.get("participantId").toString());
        
        // Send confirmation back to the participant
        Map<String, Object> response = Map.of(
                "type", "join.confirmed",
                "contestId", contestId,
                "participantId", participantId,
                "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSendToUser(
                participantId.toString(), 
                "/queue/contest." + contestId, 
                response
        );
    }

    @MessageMapping("/contest.leave")
    public void leaveContest(@Payload Map<String, Object> payload) {
        log.info("WebSocket leave contest request: {}", payload);
        Long contestId = Long.valueOf(payload.get("contestId").toString());
        Long participantId = Long.valueOf(payload.get("participantId").toString());
        
        // Send confirmation back to the participant
        Map<String, Object> response = Map.of(
                "type", "leave.confirmed",
                "contestId", contestId,
                "participantId", participantId,
                "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSendToUser(
                participantId.toString(), 
                "/queue/contest." + contestId, 
                response
        );
    }

    @MessageMapping("/answer.submit")
    public void submitAnswer(@Payload Map<String, Object> payload) {
        log.info("WebSocket answer submission: {}", payload);
        Long contestId = Long.valueOf(payload.get("contestId").toString());
        Long participantId = Long.valueOf(payload.get("participantId").toString());
        
        // Send confirmation back to the participant
        Map<String, Object> response = Map.of(
                "type", "answer.received",
                "contestId", contestId,
                "participantId", participantId,
                "submissionId", payload.get("submissionId"),
                "timestamp", System.currentTimeMillis()
        );
        
        messagingTemplate.convertAndSendToUser(
                participantId.toString(), 
                "/queue/contest." + contestId, 
                response
        );
    }
}
