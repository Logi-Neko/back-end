package exe2.learningapp.logineko.quizziz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
@Schema(name = "Participant")
public class ParticipantDTO {
    @Builder
    public record ParticipantRequest(
            Long contestId,
            Long userId
    ){}
    @Builder
    public record ParticipantResponse(
            Long id,
            Long contestId,
            Long userId,
            Integer score,
            LocalDateTime joinAt
    ){}
}
