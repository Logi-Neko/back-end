package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;


import java.util.List;
import java.util.Optional;

public interface AnswerOptionService {
//    AnswerOptionDTO.AnswerOptionResponse create(AnswerOptionDTO.AnswerOptionRequest request);
    AnswerOptionDTO.AnswerOptionResponse update(Long id, AnswerOptionDTO.AnswerOptionRequest request);
    void delete(Long id);
    Optional<AnswerOptionDTO.AnswerOptionResponse> findById(Long id);
    List<AnswerOptionDTO.AnswerOptionResponse> findByQuestion(Long questionId);
}
