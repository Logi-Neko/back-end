package exe2.learningapp.logineko.authentication.dtos.websocket;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationMessage(
        String type,
        String title,
        String message,
        String senderId,
        String senderName,
        String senderAvatar,
        Object data,
        LocalDateTime timestamp
) {

    public enum NotificationType {
        FRIEND_REQUEST,
        FRIEND_REQUEST_ACCEPTED,
        FRIEND_REQUEST_DECLINED,
        SYSTEM_NOTIFICATION
    }
}
