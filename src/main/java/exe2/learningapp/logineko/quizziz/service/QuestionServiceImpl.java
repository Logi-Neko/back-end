package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;
import exe2.learningapp.logineko.quizziz.dto.GameEventDTO;
import exe2.learningapp.logineko.quizziz.dto.QuestionDTO;
import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import exe2.learningapp.logineko.quizziz.entity.ContestQuestion;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.repository.ContestQuestionRepository;
import exe2.learningapp.logineko.quizziz.repository.QuestionRepository;
import exe2.learningapp.logineko.quizziz.service.kafka.EventProducer;
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
    private final ContestQuestionRepository contestQuestionRepository;
    private final EventProducer eventProducer;

    @Override
    public QuestionDTO.QuestionResponse createQuestion(QuestionDTO.QuestionRequest questionRequest) {
        Question question = Question.builder()
                .questionText(questionRequest.questionText())
                .timeLimit(questionRequest.timeLimit() != null ? questionRequest.timeLimit() : 30)
                .points(questionRequest.points() != null ? questionRequest.points() : 1000)
                .build();

        if (questionRequest.answerOptions() != null) {
            List<AnswerOption> options = questionRequest.answerOptions().stream()
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
    public QuestionDTO.QuestionResponse updateQuestion(Long id, QuestionDTO.QuestionRequest questionRequest) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));

        question.setQuestionText(questionRequest.questionText());
        question.setPoints(questionRequest.points());
        question.setTimeLimit(questionRequest.timeLimit());

        if (questionRequest.answerOptions() != null) {
            question.getOptions().clear();
            List<AnswerOption> options = questionRequest.answerOptions().stream()
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
    public QuestionDTO.QuestionResponse findById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));
        return toResponse(question);
    }

    @Override
    public Page<QuestionDTO.QuestionResponse> search(String textQuestion, Pageable pageable) {
        return questionRepository.findByQuestionTextContainingIgnoreCase(textQuestion, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<QuestionDTO.QuestionResponse> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public void revealQuestion(Long contestId, Long contestQuestionId) {
            ContestQuestion cq = contestQuestionRepository.findById(contestQuestionId)
                    .orElseThrow(() -> new EntityNotFoundException("ContestQuestion not found with id: " + contestQuestionId));

            Question q = cq.getQuestion();
            QuestionDTO.QuestionResponse questionResponse = toResponse(q);
        GameEventDTO.QuestionRevealedEvent event = GameEventDTO.QuestionRevealedEvent.builder()
                    .eventType("question.revealed")
                    .contestId(contestId)
                    .contestQuestionId(cq.getId())
                    .orderIndex(cq.getIndex())
                    .question(questionResponse)
                    .timestamp(java.time.Instant.now())
                    .build();
            // Here you would typically publish the event to a message broker or WebSocket
            // For this example, we'll just log it
            System.out.println("Revealed Question Event: " + event);
            eventProducer.publishQuestionRevealed(contestId, event);
    }

    private QuestionDTO.QuestionResponse toResponse(Question q) {
        return QuestionDTO.QuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .points(q.getPoints())
                .timeLimit(q.getTimeLimit())
                .answerOptions(q.getOptions() != null
                        ? q.getOptions().stream()
                        .map(opt -> AnswerOptionDTO.AnswerOptionResponse.builder()
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
