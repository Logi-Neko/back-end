package exe2.learningapp.logineko.quizziz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class RoomDTO {

    @Builder
    public record CreateRoomRequest(
            @NotBlank(message = "Title cannot be empty")
            @Size(min = 2, max = 30, message = "title must be between 2 and 30 characters")
            String title,
            @NotBlank(message = "Description cannot be empty")
            @Size(min = 2, max = 100, message = "description must be between 2 and 100 characters")
            String description
    )
    { }
    @Builder
    public record RoomResponse(
            Long id,
            String code,
            String title,
            String description,
            boolean isPublic
    )
    { }

    @Builder
    public record UpdateRoom(
            @NotBlank(message = "Title cannot be empty")
            @Size(min = 2, max = 30, message = "title must be between 2 and 30 characters")
            String title,
            @NotBlank(message = "Description cannot be empty")
            @Size(min = 2, max = 100, message = "description must be between 2 and 100 characters")
            String description,
            boolean isPublic
    )
    { }
    }

