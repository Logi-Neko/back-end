package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDTO {
    Long id;
    String name;
    String description;
    String thumbnailUrl;
    String thumbnailPublicId;
    Long totalLesson;
    Boolean isPremium;
    Boolean isActive;
    Long price;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
