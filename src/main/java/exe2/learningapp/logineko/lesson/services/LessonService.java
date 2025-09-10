package exe2.learningapp.logineko.lesson.services;

import exe2.learningapp.logineko.lesson.dtos.requests.LessonFilterRequest;
import exe2.learningapp.logineko.lesson.dtos.requests.LessonRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.LessonDTO;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LessonService {
    LessonDTO create(LessonRequest request, MultipartFile thumbnail);

    LessonDTO update(Long id, LessonRequest request, MultipartFile thumbnail);

    void delete(Long id);

    LessonDTO findById(Long id);

    List<LessonDTO> findAll();

    List<LessonDTO> search(LessonFilterRequest request);
}
