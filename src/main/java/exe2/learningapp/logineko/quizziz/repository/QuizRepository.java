package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  QuizRepository  extends JpaRepository<Quiz, Long> {
}
