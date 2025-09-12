package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findByParticipantId(Long participantId, Pageable pageable);
    Page<Answer> findByContestQuestionId(Long contestQuestionId, Pageable pageable);
    boolean existsBySubmissionUuid(Long submissionUuid);
    Optional<Answer> findBySubmissionUuid(Long submissionUuid);

}
