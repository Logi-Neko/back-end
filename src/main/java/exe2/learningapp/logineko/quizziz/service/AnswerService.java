package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AnswerService {
    AnswerDTO.Response create(AnswerDTO.CreateRequest request);
    void delete(Long id);
    Optional<AnswerDTO.Response> findById(Long id);
    Page<AnswerDTO.Response> findByParticipant(Long participantId, Pageable pageable);
    Page<AnswerDTO.Response> findByContestQuestion(Long contestQuestionId, Pageable pageable);

    boolean existsBySubmissionUuid(Long submissionUuid);
    void saveFromEvent(
            Long submissionUuid,
            Long participantId,
            Long contestQuestionId,
            Long answerOptionId,
            boolean isCorrect,
            int score,
            int answerTime
    );
}
