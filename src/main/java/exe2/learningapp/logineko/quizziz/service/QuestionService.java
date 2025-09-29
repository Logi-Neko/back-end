package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {
    QuestionDTO.QuestionResponse createQuestion(QuestionDTO.QuestionRequest questionRequest);
    QuestionDTO.QuestionResponse updateQuestion(Long id, QuestionDTO.QuestionRequest questionRequest);
    void deleteQuestion(Long id);
    QuestionDTO.QuestionResponse findById(Long id);
    Page<QuestionDTO.QuestionResponse> search(String textQuestion, Pageable pageable);
    Page<QuestionDTO.QuestionResponse> findAll(Pageable pageable);
   // void startQuestion(Integer questionNo);
    void revealQuestion(Long contestId,Long contestQuestionId);
}
