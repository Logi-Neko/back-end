package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.ContestQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestQuestionRepository  extends JpaRepository<ContestQuestion, Long> {
    Page<ContestQuestion> findByContest_Id(Long contestId, Pageable pageable);
}
