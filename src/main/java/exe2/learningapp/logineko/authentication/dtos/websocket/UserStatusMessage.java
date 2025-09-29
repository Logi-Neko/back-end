package exe2.learningapp.logineko.authentication.dtos.websocket;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserStatusMessage(
        String userId,
        String username,
        String status,
        LocalDateTime lastSeen
) {

    public enum UserStatus {
        ONLINE,
        OFFLINE,
        AWAY,
        BUSY
    }
}
