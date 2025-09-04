package exe2.learningapp.logineko.lesson.dtos.responses;

import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.enums.VideoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    LessonDTO lesson;
}
