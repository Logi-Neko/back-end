package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;


import java.util.List;

public interface AnswerOptionService {
    AnswerOptionDTO.Response create(AnswerOptionDTO.Request request);
    AnswerOptionDTO.Response update(Long id, AnswerOptionDTO.Request request);
    void delete(Long id);
    AnswerOptionDTO.Response findById(Long id);
    List<AnswerOptionDTO.Response> findAllByQuestionId(Long questionId);
}
