package exe2.learningapp.logineko.lesson.services;

import exe2.learningapp.logineko.lesson.dtos.requests.CourseRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.CourseDTO;
import exe2.learningapp.logineko.lesson.entities.Course;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    CourseDTO create(CourseRequest request, MultipartFile thumbnail);

    CourseDTO update(Long id, CourseRequest request, MultipartFile thumbnail);

    CourseDTO findById(Long id);

    List<CourseDTO> findAll();

    CourseDTO convertToCourseDTO(Course course);
}
