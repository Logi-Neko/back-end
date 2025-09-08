package exe2.learningapp.logineko.lesson.repositories;

import exe2.learningapp.logineko.lesson.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {
}
