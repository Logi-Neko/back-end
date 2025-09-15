package exe2.learningapp.logineko.quizziz.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class ParticipantDTO {
    @Builder
    public record Request(
            Long contestId,
            Long userId
    ){}
    @Builder
    public record Response(
            Long id,
            Long contestId,
            Long userId,
            Integer score,
            LocalDateTime joinAt
    ){}
}
