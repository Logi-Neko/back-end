package exe2.learningapp.logineko.lesson.dtos.requests;

import jakarta.validation.constraints.Min;
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
public class CourseRequest {
    @NotBlank(message = "Tên khóa học không được rỗng")
    @NotNull(message = "Tên khóa học là bắt buộc")
    String name;

    @NotBlank(message = "Mô tả khóa học không được rỗng")
    @NotNull(message = "Mô tả khóa học là bắt buộc")
    String description;

    Boolean isPremium = false;

    Boolean isActive = true;

    @Min(value = 1000, message = "Giá phải lớn hơn 1000")
    Long price;
}
