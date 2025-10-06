package exe2.learningapp.logineko.authentication.dtos.websocket;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessage(
        String id,
        String senderId,
        String senderName,
        String senderAvatar,
        String receiverId,
        String content,
        String messageType,
        LocalDateTime timestamp,
        boolean isRead
) {

    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        EMOJI,
        SYSTEM
    }
}
