package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.dtos.responses.VideoQuestionDTO;
import exe2.learningapp.logineko.lesson.entities.*;
import exe2.learningapp.logineko.lesson.repositories.AccountLessonProgressRepository;
import exe2.learningapp.logineko.lesson.repositories.AccountQuestionResultRepository;
import exe2.learningapp.logineko.lesson.repositories.VideoQuestionRepository;
import exe2.learningapp.logineko.lesson.repositories.VideoRepository;
import exe2.learningapp.logineko.lesson.services.VideoQuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VideoQuestionServiceImpl implements VideoQuestionService {
    CurrentUserProvider currentUserProvider;
    VideoQuestionRepository videoQuestionRepository;
    AccountQuestionResultRepository accountQuestionResultRepository;
    VideoRepository videoRepository;
    AccountLessonProgressRepository accountLessonProgressRepository;

    @Override
    @Transactional
    public void answerQuestion(Long videoQuestionId, String answer) {
        Account account = currentUserProvider.getCurrentUser();

        VideoQuestion videoQuestion = videoQuestionRepository.findById(videoQuestionId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        Video video = videoRepository.findByVideoQuestion(videoQuestion);

        Lesson lesson = video.getLesson();

        AccountQuestionResult result = AccountQuestionResult.builder()
                .account(account)
                .video(video)
                .isCorrect(answer.equalsIgnoreCase(videoQuestion.getAnswer()))
                .build();

        accountQuestionResultRepository.save(result);

        long totalVideoQuestion = videoRepository.countByLesson(lesson);
        long completedVideoQuestion = accountQuestionResultRepository.countDistinctCorrectQuestionsByAccountAndLesson(account, lesson);
        double completePercentage = (double) completedVideoQuestion / totalVideoQuestion;

        AccountLessonProgress lessonProgress = accountLessonProgressRepository.findByLessonAndAccount(lesson, account);

        if (lessonProgress == null) {
            lessonProgress = AccountLessonProgress.builder()
                    .account(account)
                    .lesson(lesson)
                    .star((long) (completePercentage * 5))
                    .build();

        } else {
            lessonProgress.setStar((long) (completePercentage * 5));
        }
        accountLessonProgressRepository.save(lessonProgress);
    }

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
