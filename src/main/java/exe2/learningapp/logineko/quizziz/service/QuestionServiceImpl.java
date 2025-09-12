package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;
import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.repository.QuestionRepository;
import exe2.learningapp.logineko.quizziz.service.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public QuestionDTO.Response createQuestion(QuestionDTO.Request request) {
        Question question = Question.builder()
                .questionText(request.questionText())
                .timeLimit(request.timeLimit() != null ? request.timeLimit() : 30)
                .points(request.points() != null ? request.points() : 1000)
                .build();

        if (request.answerOptions() != null) {
            List<AnswerOption> options = request.answerOptions().stream()
                    .map(opt -> AnswerOption.builder()
                            .optionText(opt.optionText())
                            .isCorrect(opt.isCorrect())
                            .question(question)
                            .build())
                    .collect(Collectors.toList());
            question.setOptions(options);
        }

        Question saved = questionRepository.save(question);
        return toResponse(saved);
    }

    @Override
    public QuestionDTO.Response updateQuestion(Long id, QuestionDTO.Request request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));

        question.setQuestionText(request.questionText());
        question.setPoints(request.points());
        question.setTimeLimit(request.timeLimit());

        if (request.answerOptions() != null) {
            question.getOptions().clear();
            List<AnswerOption> options = request.answerOptions().stream()
                    .map(opt -> AnswerOption.builder()
                            .optionText(opt.optionText())
                            .isCorrect(opt.isCorrect())
                            .question(question)
                            .build())
                    .collect(Collectors.toList());
            question.getOptions().addAll(options);
        }

        Question updated = questionRepository.save(question);
        return toResponse(updated);
    }

    @Override
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new EntityNotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
    }

    @Override
    public QuestionDTO.Response findById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));
        return toResponse(question);
    }

    @Override
    public Page<QuestionDTO.Response> search(String textQuestion, Pageable pageable) {
        return questionRepository.findByQuestionTextContainingIgnoreCase(textQuestion, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<QuestionDTO.Response> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private QuestionDTO.Response toResponse(Question q) {
        return QuestionDTO.Response.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .points(q.getPoints())
                .timeLimit(q.getTimeLimit())
                .answerOptions(q.getOptions() != null
                        ? q.getOptions().stream()
                        .map(opt -> AnswerOptionDTO.Response.builder()
                                .id(opt.getId())
                                .optionText(opt.getOptionText())
                                .isCorrect(opt.getIsCorrect())
                                .build())
                        .collect(Collectors.toList())
                        : List.of()
                )
                .build();
    }
}
