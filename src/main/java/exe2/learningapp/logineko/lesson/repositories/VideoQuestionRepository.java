package exe2.learningapp.logineko.lesson.repositories;

import exe2.learningapp.logineko.lesson.entities.VideoQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoQuestionRepository extends JpaRepository<VideoQuestion, Long> {
}
