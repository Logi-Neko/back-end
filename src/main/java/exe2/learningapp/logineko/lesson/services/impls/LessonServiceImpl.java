package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.authentication.component.CurrentUserProvider;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.dtos.requests.LessonFilterRequest;
import exe2.learningapp.logineko.lesson.dtos.requests.LessonRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.LessonDTO;
import exe2.learningapp.logineko.lesson.entities.AccountLessonProgress;
import exe2.learningapp.logineko.lesson.entities.Course;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.Video;
import exe2.learningapp.logineko.lesson.repositories.AccountLessonProgressRepository;
import exe2.learningapp.logineko.lesson.repositories.CourseRepository;
import exe2.learningapp.logineko.lesson.repositories.LessonRepository;
import exe2.learningapp.logineko.lesson.repositories.VideoRepository;
import exe2.learningapp.logineko.lesson.repositories.specifications.LessonSpecifications;
import exe2.learningapp.logineko.lesson.services.FileService;
import exe2.learningapp.logineko.lesson.services.LessonService;
import exe2.learningapp.logineko.lesson.services.VideoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    LessonRepository lessonRepository;
    FileService fileService;
    VideoRepository videoRepository;
    VideoService videoService;
    CourseRepository courseRepository;
    AccountLessonProgressRepository accountLessonProgressRepository;
    CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public LessonDTO create(LessonRequest request, MultipartFile thumbnail) {
        Course course = courseRepository.findById(request.getCourseId()).
                orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        Lesson lesson = Lesson
                .builder()
                .name(request.getName())
                .description(request.getDescription())
                .index(request.getOrder())
                .minAge(request.getMinAge())
                .maxAge(request.getMaxAge())
                .difficultyLevel(request.getDifficultyLevel())
                .duration(request.getDuration())
                .isPremium(request.getIsPremium())
                .isActive(request.getIsActive())
                .course(course)
                .build();

        lessonRepository.save(lesson);

        Pair<String, String> fileData;
        try {
            fileData = fileService.uploadFile(thumbnail, "/courses/" + course.getId() + "/lessons/" + lesson.getId());

            lesson.setThumbnailUrl(fileData.getFirst());
            lesson.setThumbnailPublicId(fileData.getSecond());
            lessonRepository.save(lesson);
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }

        course.setTotalLesson(course.getTotalLesson() + 1);
        courseRepository.save(course);

        return convertToLessonDTO(lesson);
    }

    @Override
    @Transactional
    public LessonDTO update(Long id, LessonRequest request, MultipartFile thumbnail) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        Course course = courseRepository.findById(request.getCourseId()).
                orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        String oldThumbnailPublicId = lesson.getThumbnailPublicId();

        lesson.setName(request.getName());
        lesson.setDescription(request.getDescription());
        lesson.setIndex(request.getOrder());
        lesson.setMinAge(request.getMinAge());
        lesson.setMaxAge(request.getMaxAge());
        lesson.setDifficultyLevel(request.getDifficultyLevel());
        lesson.setDuration(request.getDuration());
        lesson.setIsPremium(request.getIsPremium());
        lesson.setIsActive(request.getIsActive());
        lesson.setCourse(course);

        if (thumbnail != null) {
            Pair<String, String> fileData;
            try {
                fileData = fileService.uploadFile(thumbnail, "/courses/" + course.getId() + "/lessons/" + lesson.getId());

                lesson.setThumbnailUrl(fileData.getFirst());
                lesson.setThumbnailPublicId(fileData.getSecond());
                lessonRepository.save(lesson);
            } catch (IOException e) {
                throw new AppException(ErrorCode.ERR_SERVER_ERROR);
            }

            try {
                fileService.deleteFile(oldThumbnailPublicId);
            } catch (IOException ignored) {
            }
        } else {
            lessonRepository.save(lesson);
        }

        return convertToLessonDTO(lesson);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        List<Video> videos = videoRepository.findByLesson_Id(id);
        for (Video video : videos) {
            videoService.delete(video.getId());
        }

        try {
            fileService.deleteFile(lesson.getThumbnailPublicId());
        } catch (IOException ignored) {
        }

        Course course = lesson.getCourse();
        course.setTotalLesson(course.getTotalLesson() - 1);
        courseRepository.save(course);

        lessonRepository.delete(lesson);
    }

    @Override
    public LessonDTO findById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return convertToLessonDTO(lesson);
    }

    @Override
    public List<LessonDTO> findAll() {
        return lessonRepository.findAll()
                .stream()
                .map(this::convertToLessonDTO)
                .toList();
    }

    @Override
    public List<LessonDTO> findByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return lessonRepository.findByCourse(course)
                .stream()
                .map(this::convertToLessonDTO)
                .toList();
    }

    @Override
    public List<LessonDTO> search(LessonFilterRequest request) {
        Specification<Lesson> spec = Specification.allOf(
                LessonSpecifications.hasName(request.getName()),
                LessonSpecifications.hasDescription(request.getDescription()),
                LessonSpecifications.hasOrder(request.getOrder()),
                LessonSpecifications.hasMinAge(request.getMinAge()),
                LessonSpecifications.hasMaxAge(request.getMaxAge()),
                LessonSpecifications.hasDifficultyLevel(request.getDifficultyLevel()),
                LessonSpecifications.hasDuration(request.getDuration()),
                LessonSpecifications.isPremium(request.getIsPremium()),
                LessonSpecifications.createdAfter(request.getCreatedAfter()),
                LessonSpecifications.createdBefore(request.getCreatedBefore())
        );

        return lessonRepository.findAll(spec)
                .stream()
                .map(this::convertToLessonDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDTO convertToLessonDTO(Lesson lesson) {
        AccountLessonProgress accountLessonProgress = null;
        Account account = currentUserProvider.getCurrentUser();
        if (account != null) {
            accountLessonProgress = accountLessonProgressRepository.findByLessonAndAccount(lesson, account);
        }

        return LessonDTO
                .builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .order(lesson.getIndex())
                .minAge(lesson.getMinAge())
                .maxAge(lesson.getMaxAge())
                .difficultyLevel(lesson.getDifficultyLevel())
                .thumbnailUrl(lesson.getThumbnailUrl())
                .duration(lesson.getDuration())
                .star(accountLessonProgress != null ? accountLessonProgress.getStar() : 0)
                .isPremium(lesson.getIsPremium())
                .isActive(lesson.getIsActive())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
