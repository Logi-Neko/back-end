package exe2.learningapp.logineko.lesson.repositories;

import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.Video;
import exe2.learningapp.logineko.lesson.entities.VideoQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByLesson_Id(Long id);

    Video findByVideoQuestion(VideoQuestion videoQuestion);

    long countByLesson(Lesson lesson);
}
