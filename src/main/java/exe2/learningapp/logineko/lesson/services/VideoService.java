package exe2.learningapp.logineko.lesson.services;

import exe2.learningapp.logineko.lesson.dtos.requests.VideoRequest;
import exe2.learningapp.logineko.lesson.dtos.responses.VideoDTO;
import exe2.learningapp.logineko.lesson.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    VideoDTO create(VideoRequest request, MultipartFile thumbnail, MultipartFile video);

    VideoDTO update(Long id, VideoRequest request, MultipartFile thumbnail, MultipartFile video);

    void delete(Long id);

    List<VideoDTO> findByLessonId(Long lessonId);

    VideoDTO convertToDTO(Video video);
}
