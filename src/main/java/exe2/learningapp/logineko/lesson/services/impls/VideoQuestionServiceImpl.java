package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.lesson.dtos.responses.VideoQuestionDTO;
import exe2.learningapp.logineko.lesson.entities.VideoQuestion;
import exe2.learningapp.logineko.lesson.services.VideoQuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VideoQuestionServiceImpl implements VideoQuestionService {
    @Override
    public VideoQuestionDTO convertToVideoQuestionDTO(VideoQuestion videoQuestion) {
        return VideoQuestionDTO.builder()
                .id(videoQuestion.getId())
                .question(videoQuestion.getQuestion())
                .optionA(videoQuestion.getOptionA())
                .optionB(videoQuestion.getOptionB())
                .optionC(videoQuestion.getOptionC())
                .optionD(videoQuestion.getOptionD())
                .answer(videoQuestion.getAnswer())
                .build();
    }
}
