package exe2.learningapp.logineko.lesson.dtos.requests;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonFilterRequest {
    String name;
    String description;
    Long order;
    Long minAge;
    Long maxAge;
    Long difficultyLevel;
    Long duration;
    Boolean isPremium;
    LocalDateTime createdAfter;
    LocalDateTime createdBefore;
}
