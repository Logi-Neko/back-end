package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AnswerService {
    AnswerDTO.AnswerResponse create(AnswerDTO.AnswerRequest request);
    void delete(Long id);
    Optional<AnswerDTO.AnswerResponse> findById(Long id);
    Page<AnswerDTO.AnswerResponse> findByParticipant(Long participantId, Pageable pageable);
    Page<AnswerDTO.AnswerResponse> findByContestQuestion(Long contestQuestionId, Pageable pageable);

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
    void saveAnswer(AnswerDTO.AnswerResponse answerResponse);
    void updateAnswerScore(Long answerId, boolean isCorrect, int score);
}
