package exe2.learningapp.logineko.lesson.services;

import exe2.learningapp.logineko.lesson.dtos.responses.VideoQuestionDTO;
import exe2.learningapp.logineko.lesson.entities.VideoQuestion;

public interface VideoQuestionService {
    void answerQuestion(Long videoQuestionId, String answer);

    VideoQuestionDTO convertToVideoQuestionDTO(VideoQuestion videoQuestion);
}
