package exe2.learningapp.logineko.lesson.repositories;

import exe2.learningapp.logineko.lesson.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
