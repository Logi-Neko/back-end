package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.dtos.requests.CourseRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.CourseDTO;
import exe2.learningapp.logineko.lesson.entities.Course;
import exe2.learningapp.logineko.lesson.repositories.CourseRepository;
import exe2.learningapp.logineko.lesson.services.CourseService;
import exe2.learningapp.logineko.lesson.services.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    CourseRepository courseRepository;
    FileService fileService;

    @Override
    @Transactional
    public CourseDTO create(CourseRequest request, MultipartFile thumbnail) {
        Course course = Course.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isPremium(request.getIsPremium())
                .isActive(request.getIsActive())
                .price(request.getPrice())
                .build();

        courseRepository.save(course);

        Pair<String, String> thumbnailData;
        try {
            thumbnailData = fileService.uploadFile(thumbnail, "/courses/" + course.getId());

            course.setThumbnailUrl(thumbnailData.getFirst());
            course.setThumbnailPublicId(thumbnailData.getSecond());
            courseRepository.save(course);
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERR_SERVER_ERROR);
        }

        return convertToCourseDTO(course);
    }

    @Override
    @Transactional
    public CourseDTO update(Long id, CourseRequest request, MultipartFile thumbnail) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        String oldThumbnailPublicId = course.getThumbnailPublicId();

        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setIsPremium(request.getIsPremium());
        course.setIsActive(request.getIsActive());
        course.setPrice(request.getPrice());

        if (thumbnail != null) {
            Pair<String, String> fileData;
            try {
                fileData = fileService.uploadFile(thumbnail, "/courses/" + course.getId());

                course.setThumbnailUrl(fileData.getFirst());
                course.setThumbnailPublicId(fileData.getSecond());
                courseRepository.save(course);
            } catch (IOException e) {
                throw new AppException(ErrorCode.ERR_SERVER_ERROR);
            }

            try {
                fileService.deleteFile(oldThumbnailPublicId);
            } catch (IOException ignored) {
            }
        } else {
            courseRepository.save(course);
        }

        return convertToCourseDTO(course);
    }

    @Override
    public CourseDTO findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return convertToCourseDTO(course);
    }

    @Override
    public List<CourseDTO> findAll() {
        return courseRepository.findAll()
                .stream()
                .map(this::convertToCourseDTO)
                .toList();
    }

    @Override
    public CourseDTO convertToCourseDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .thumbnailPublicId(course.getThumbnailPublicId())
                .totalLesson(course.getTotalLesson())
                .isPremium(course.getIsPremium())
                .isActive(course.getIsActive())
                .price(course.getPrice())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
