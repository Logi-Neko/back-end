package exe2.learningapp.logineko.lesson.repositories;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.lesson.entities.AccountQuestionResult;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountQuestionResultRepository extends JpaRepository<AccountQuestionResult, Long> {
    @Query("SELECT COUNT(DISTINCT r.video.id) " +
            "FROM AccountQuestionResult r " +
            "WHERE r.account = :account " +
            "AND r.video.lesson = :lesson " +
            "AND r.isCorrect = true")
    long countDistinctCorrectQuestionsByAccountAndLesson(
            @Param("account") Account account,
            @Param("lesson") Lesson lesson
    );
}
