package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDTO {
    Long id;
    String name;
    String description;
    Long order;
    Long minAge;
    Long maxAge;
    Long difficultyLevel;
    String thumbnailUrl;
    Long duration;
    Boolean isPremium;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
