package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;
import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.entity.Quiz;
import exe2.learningapp.logineko.quizziz.repository.AnswerOptionRepository;
import exe2.learningapp.logineko.quizziz.repository.QuestionRepository;
import exe2.learningapp.logineko.quizziz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final AnswerOptionRepository answerOptionRepository;
    @Override
    public QuestionDTO.Response createQuestion(QuestionDTO.Request request) {
        if (request.quizId() == null) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }
        Quiz quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));
        Question question = mapToEntity(request, quiz);
        Question savedQuestion = questionRepository.save(question);
        if (request.answerOptions() != null && !request.answerOptions().isEmpty()) {
            List<AnswerOption> options = request.answerOptions().stream()
                    .map(dto -> AnswerOption.builder()
                            .question(savedQuestion)
                            .optionLabel(dto.optionLabel())
                            .optionText(dto.optionText())
                            .isCorrect(dto.isCorrect())
                            .build())
                    .collect(Collectors.toList());
            answerOptionRepository.saveAll(options);
        }
        return mapToDto(questionRepository.save(question));
    }

    @Override
    public QuestionDTO.Response updateQuestion(Long id, QuestionDTO.Request request) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        existingQuestion.setQuestionText(request.questionText());
        existingQuestion.setTimeLimit(request.timeLimit());
        existingQuestion.setPoints(request.points() != null ? request.points() : existingQuestion.getPoints());
        Question updatedQuestion = questionRepository.save(existingQuestion);
        if (request.answerOptions() != null) {
            answerOptionRepository.deleteAll(existingQuestion.getOptions());
            List<AnswerOption> newOptions = request.answerOptions().stream()
                    .map(dto -> AnswerOption.builder()
                            .question(existingQuestion)
                            .optionLabel(dto.optionLabel())
                            .optionText(dto.optionText())
                            .isCorrect(dto.isCorrect())
                            .build())
                    .collect(Collectors.toList());
            answerOptionRepository.saveAll(newOptions);
            existingQuestion.setOptions(newOptions);
        }
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
        Page<Question> questions = questionRepository.findByQuizId(quizId,pageable);
        return questions.map(this::mapToDto);
    }
    private QuestionDTO.Response mapToDto(Question question) {
        List<AnswerOptionDTO.Response> optionResponses = question.getOptions().stream()
                .map(option -> AnswerOptionDTO.Response.builder()
                        .id(option.getId())
                        .optionLabel(option.getOptionLabel())
                        .optionText(option.getOptionText())
                        .isCorrect(option.getIsCorrect())
                        .questionId(option.getQuestion().getId())
                        .build())
                .collect(Collectors.toList());

        return QuestionDTO.Response.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .answerOptions(optionResponses)
                .points(question.getPoints())
                .timeLimit(question.getTimeLimit())
                .quizId(question.getQuiz() != null ? question.getQuiz().getQuizId() : null)
                .build();
    }

    private Question mapToEntity(QuestionDTO.Request request, Quiz quiz) {
        Question question = new Question();
        question.setQuestionText(request.questionText());
        question.setTimeLimit(request.timeLimit());
        question.setQuiz(quiz);
        question.setPoints(request.points() != null ? request.points() : 1000);
        question.setQuestionNo(quiz.getQuestions().size() + 1);

        return question;
    }
}
