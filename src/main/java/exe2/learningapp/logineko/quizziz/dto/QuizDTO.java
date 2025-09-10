package exe2.learningapp.logineko.quizziz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

public class QuizDTO {
    @Builder
    public record Request(
            @NotBlank(message = "Room ID cannot be empty")
            Long roomId,
            @NotBlank(message = "Name cannot be empty")
            @Size(min = 2, max = 30, message = "name must be between 2 and 30 characters")
            String name,
            @NotBlank(message = "Description cannot be empty")
            @Size(min = 2, max = 100, message = "description must be between 2 and 100 characters")
            String description,
            @Positive(message = "Duration must be positive")
            @Size(min = 1, message = "Duration must be greater than 0")
            Integer duration,
            @Positive(message = "Total questions must be positive")
            @Size(min = 1, message = "Total questions must be greater than 0")
            Integer totalQuestions
    )
    {}
    @Builder
    public record Response(
            Long id,
            String name,
            String description,
            Integer duration,
            Integer totalQuestions,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long roomId
    )
    {}
}
