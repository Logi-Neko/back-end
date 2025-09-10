package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoQuestionDTO {
    Long id;
    String question;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    String answer;
}
