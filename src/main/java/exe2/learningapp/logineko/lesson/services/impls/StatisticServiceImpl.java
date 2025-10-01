package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.dtos.responses.CourseDTO;
import exe2.learningapp.logineko.lesson.dtos.responses.StatisticDTO;
import exe2.learningapp.logineko.lesson.entities.AccountQuestionResult;
import exe2.learningapp.logineko.lesson.entities.Course;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.Video;
import exe2.learningapp.logineko.lesson.repositories.AccountQuestionResultRepository;
import exe2.learningapp.logineko.lesson.services.CourseService;
import exe2.learningapp.logineko.lesson.services.LessonService;
import exe2.learningapp.logineko.lesson.services.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    AccountRepository accountRepository;
    AccountQuestionResultRepository accountQuestionResultRepository;
    LessonService lessonService;
    CourseService courseService;

    @Override
    @Transactional
    public StatisticDTO getStatistic(Long accountId, LocalDate from, LocalDate to) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        List<AccountQuestionResult> results = accountQuestionResultRepository.findByAccountAndCreatedAtBetween(
                account,
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay()
        );

        List<Video> videos = results.stream()
                .map(AccountQuestionResult::getVideo)
                .collect(Collectors.toMap(Video::getId, v -> v, (v1, v2) -> v1))
                .values().stream()
                .toList();

        List<Lesson> lessons = videos.stream()
                .map(Video::getLesson)
                .collect(Collectors.toMap(Lesson::getId, v -> v, (v1, v2) -> v1))
                .values().stream()
                .toList();

        List<CourseDTO> courses = lessons.stream()
                .map(Lesson::getCourse)
                .collect(Collectors.toMap(Course::getId, v -> v, (v1, v2) -> v1))
                .values().stream()
                .map(courseService::convertToCourseDTO)
                .toList();

        return StatisticDTO.builder()
                .courses(courses)
                .lessons(lessons.stream().map(lessonService::convertToLessonDTO).toList())
                .from(from)
                .to(to)
                .build();
    }
}
