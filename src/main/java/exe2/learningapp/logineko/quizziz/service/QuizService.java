package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.QuizDTO;

import java.util.List;
import java.util.Optional;

public interface QuizService {
  QuizDTO.Response createQuiz(QuizDTO.Request request);
  Optional<QuizDTO.Response> findById(Long id);
  List<QuizDTO.Response> findByRoom(Long roomId);
  void deleteQuiz(Long id);
  QuizDTO.Response updateQuiz(Long id, QuizDTO.Request request);
  QuizDTO.Response startQuiz(Long id);
}
