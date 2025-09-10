package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.entity.Quiz;
import exe2.learningapp.logineko.quizziz.repository.QuestionRepository;
import exe2.learningapp.logineko.quizziz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    @Override
    public QuestionDTO.Response createQuestion(QuestionDTO.Request request) {
        if (request.quizId() == null) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }
        Quiz quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));
        Question question = mapToEntity(request, quiz);
        return mapToDto(questionRepository.save(question));
    }

    @Override
    public QuestionDTO.Response updateQuestion(Long id, QuestionDTO.Request request) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        existingQuestion.setQuestionText(request.questionText());
        existingQuestion.setOptions(request.answerOptions());

        Question updatedQuestion = questionRepository.save(existingQuestion);
        return mapToDto(updatedQuestion);
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new EntityNotFoundException("Question not found");
        }
        questionRepository.deleteById(id);
    }

    @Override
    public QuestionDTO.Response findById(Long id) {
        return questionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
    }



    @Override
    public Page<QuestionDTO.Response> search(String textQuestion, Pageable pageable) {
        if (textQuestion == null || textQuestion.isBlank()) {
            return questionRepository.findAll(pageable)
                    .map(this::mapToDto);
        } else {
            return questionRepository.findByQuestionTextContainingIgnoreCase(textQuestion, pageable)
                    .map(this::mapToDto);
        }
    }

    @Override
    public Page<QuestionDTO.Response> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(this::mapToDto);
    }
    @Override
    public Page<QuestionDTO.Response> findByQuizId(Long quizId, Pageable pageable) {
        Page<Question> questions = questionRepository.findAll(pageable);
        return questions.map(this::mapToDto);
    }
    private QuestionDTO.Response mapToDto(Question question) {
        return QuestionDTO.Response.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .answerOptions(question.getOptions())
                .points(question.getPoints())
                .quizId(question.getQuiz() != null ? question.getQuiz().getQuizId() : null)
                .build();
    }

    private Question mapToEntity(QuestionDTO.Request request, Quiz quiz) {
        Question question = new Question();
        question.setQuestionText(request.questionText());
        question.setOptions(request.answerOptions());
        question.setQuiz(quiz);
        return question;
    }
}
