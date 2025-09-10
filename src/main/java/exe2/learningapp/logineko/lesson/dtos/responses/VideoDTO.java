package exe2.learningapp.logineko.lesson.dtos.responses;

import exe2.learningapp.logineko.lesson.entities.enums.VideoType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoDTO {
    Long id;
    String title;
    String videoUrl;
    String videoPublicId;
    String thumbnailUrl;
    String thumbnailPublicId;
    Long duration;
    Long order;
    VideoType videoType;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    VideoQuestionDTO videoQuestion;
}
