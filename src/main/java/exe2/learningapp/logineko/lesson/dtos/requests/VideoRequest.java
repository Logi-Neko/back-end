package exe2.learningapp.logineko.lesson.dtos.requests;

import exe2.learningapp.logineko.lesson.entities.enums.VideoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoRequest {
    @NotNull(message = "LessonID là bắt buộc")
    Long lessonId;

    @NotNull(message = "Tiêu đề cho video là bắt buộc")
    @NotBlank(message = "Tiêu đề cho video không được rỗng")
    String title;

    @NotNull(message = "Thứ tự video là bắt buộc")
    Long order;

    @NotNull(message = "Loại video là bắt buộc")
    VideoType type;

    Boolean isActive = true;
}
