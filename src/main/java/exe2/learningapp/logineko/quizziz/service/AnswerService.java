package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnswerService {
    AnswerDTO.Response submitAnswer(AnswerDTO.CreateRequest request);
    AnswerDTO.Response findById(Long id);
    List<AnswerDTO.Response> findAllByQuestionId(Long questionId);
    Page<AnswerDTO.Response> findAllByParticipantId(Long participantId, Pageable pageable);
}
