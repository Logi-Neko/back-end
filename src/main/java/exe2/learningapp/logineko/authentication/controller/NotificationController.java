package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.dtos.websocket.NotificationMessage;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.service.WebSocketNotificationService;
import exe2.learningapp.logineko.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final WebSocketNotificationService webSocketNotificationService;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Send system notification to specific user (Admin only)
     */
    @PostMapping("/send/{userId}")
    public ResponseEntity<ApiResponse<String>> sendNotificationToUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {
            String title = request.get("title");
            String message = request.get("message");
            webSocketNotificationService.sendSystemNotification(userId, title, message);
            return ResponseEntity.ok(ApiResponse.success("Đã gửi thông báo thành công"));


    }


    /**
     * Get list of online users
     */
    @GetMapping("/online-users")
    public ResponseEntity<ApiResponse<Map<String, String>>> getOnlineUsers() {

            Map<String, String> onlineUsers = webSocketNotificationService.getActiveUsers();
            return ResponseEntity.ok(ApiResponse.success(onlineUsers));

    }

    /**
     * Check if user is online
     */
    @GetMapping("/user/{userId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserStatus(@PathVariable String userId) {
            boolean isOnline = webSocketNotificationService.isUserOnline(userId);
            Map<String, Object> status = Map.of(
                "userId", userId,
                "isOnline", isOnline,
                "status", isOnline ? "ONLINE" : "OFFLINE"
            );
            return ResponseEntity.ok(ApiResponse.success(status));

    }
}
