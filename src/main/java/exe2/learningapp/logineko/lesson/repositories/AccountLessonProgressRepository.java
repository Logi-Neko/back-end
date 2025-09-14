package exe2.learningapp.logineko.lesson.repositories;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.lesson.entities.AccountLessonProgress;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLessonProgressRepository extends JpaRepository<AccountLessonProgress, Long> {
    AccountLessonProgress findByLessonAndAccount(Lesson lesson, Account account);
}
