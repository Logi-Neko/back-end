package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;


import java.util.List;
import java.util.Optional;

public interface AnswerOptionService {
    AnswerOptionDTO.Response create(AnswerOptionDTO.Request request);
    AnswerOptionDTO.Response update(Long id, AnswerOptionDTO.Request request);
    void delete(Long id);
    Optional<AnswerOptionDTO.Response> findById(Long id);
    List<AnswerOptionDTO.Response> findByQuestion(Long questionId);
}
