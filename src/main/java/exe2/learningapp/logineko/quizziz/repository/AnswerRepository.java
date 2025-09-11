package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);
    Page<Answer> findByParticipantId(Long participantId, Pageable pageable);
}
