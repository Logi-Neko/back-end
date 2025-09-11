package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    QuestionDTO.Response createQuestion(QuestionDTO.Request request);
    QuestionDTO.Response updateQuestion(Long id, QuestionDTO.Request request);
    void deleteQuestion(Long id);
    QuestionDTO.Response findById(Long id);
    Page<QuestionDTO.Response> search(String textQuestion, Pageable pageable);
    Page<QuestionDTO.Response> findAll(Pageable pageable);
    Page<QuestionDTO.Response> findByQuizId(Long quizId, Pageable pageable);
    List<QuestionDTO.Response> findAllByQuizId(Long quizId);
    void startQuestion(Long quizId, Integer questionNo);
}
