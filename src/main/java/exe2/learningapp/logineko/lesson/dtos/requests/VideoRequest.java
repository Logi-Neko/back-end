package exe2.learningapp.logineko.lesson.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "Câu hỏi là bắt buộc")
    @NotBlank(message = "Câu hỏi cho video không được rỗng")
    String question;

    @NotNull(message = "Câu trả lời A là bắt buộc")
    @NotBlank(message = "Câu trả lời A cho video không được rỗng")
    String optionA;

    @NotNull(message = "Câu trả lời B là bắt buộc")
    @NotBlank(message = "Câu trả lời B cho video không được rỗng")
    String optionB;

    @NotNull(message = "Câu trả lời C là bắt buộc")
    @NotBlank(message = "Câu trả lời C cho video không được rỗng")
    String optionC;

    @NotNull(message = "Câu trả lời D là bắt buộc")
    @NotBlank(message = "Câu trả lời D cho video không được rỗng")
    String optionD;

    @NotNull(message = "Đáp án là bắt buộc")
    @NotBlank(message = "Đáp án cho video không được rỗng")
    @Pattern(regexp = "[ABCD]", message = "Đáp án chỉ được phép là A, B, C hoặc D")
    String answer;

    Boolean isActive = true;
}
