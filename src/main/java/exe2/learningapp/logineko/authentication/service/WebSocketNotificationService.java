package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.websocket.NotificationMessage;
import exe2.learningapp.logineko.authentication.dtos.websocket.UserStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> activeUsers = new ConcurrentHashMap<>();

    public void sendFriendRequestNotification(String toUserId, String fromUserId, String fromUserName, String fromUserAvatar) {
        NotificationMessage notification = NotificationMessage.builder()
                .type(NotificationMessage.NotificationType.FRIEND_REQUEST.name())
                .title("Lời mời kết bạn")
                .message(fromUserName + " đã gửi lời mời kết bạn cho bạn")
                .senderId(fromUserId)
                .senderName(fromUserName)
                .senderAvatar(fromUserAvatar)
                .timestamp(LocalDateTime.now())
                .build();

        sendNotificationToUser(toUserId, notification);
        log.info("Sent friend request notification from {} to {}", fromUserId, toUserId);
    }


    public void sendSystemNotification(String toUserId, String title, String message) {
        NotificationMessage notification = NotificationMessage.builder()
                .type(NotificationMessage.NotificationType.SYSTEM_NOTIFICATION.name())
                .title(title)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        sendNotificationToUser(toUserId, notification);
        log.info("Sent system notification to {}: {}", toUserId, title);
    }

    public void sendNotificationToUser(String userId, NotificationMessage notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/ws", notification);
    }

    public Map<String, String> getActiveUsers() {
        return new ConcurrentHashMap<>(activeUsers);
    }

    public boolean isUserOnline(String userId) {
        return activeUsers.containsKey(userId);
    }
}
