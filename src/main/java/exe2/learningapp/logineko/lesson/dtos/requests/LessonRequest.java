package exe2.learningapp.logineko.lesson.dtos.requests;

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
public class LessonRequest {
    @NotBlank(message = "Tên bài học không được rỗng")
    @NotNull(message = "Tên bài học là bắt buộc")
    String name;

    @NotBlank(message = "Mô tả bài học không được rỗng")
    @NotNull(message = "Mô tả bài học là bắt buộc")
    String description;

    @NotNull(message = "Thứ tự bài học là bắt buộc")
    Long order;

    @NotNull(message = "Độ tuổi nhỏ nhất cho bài học là bắt buộc")
    Long minAge;

    @NotNull(message = "Độ tuổi lớn nhất cho bài học là bắt buộc")
    Long maxAge;

    @NotNull(message = "Độ khó của bài học là bắt buộc")
    Long difficultyLevel;

    @NotNull(message = "Thời gian bài học là bắt buộc")
    Long duration;

    Boolean isPremium = false;

    Boolean isActive = true;
}
