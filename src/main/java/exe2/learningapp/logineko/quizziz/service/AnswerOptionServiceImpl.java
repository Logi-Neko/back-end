package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;
import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.repository.AnswerOptionRepository;
import exe2.learningapp.logineko.quizziz.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerOptionServiceImpl implements AnswerOptionService {

    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;

    private AnswerOptionDTO.Response mapToDto(AnswerOption answerOption) {
        return AnswerOptionDTO.Response.builder()
                .id(answerOption.getId())
                .optionLabel(answerOption.getOptionLabel())
                .optionText(answerOption.getOptionText())
                .isCorrect(answerOption.getIsCorrect())
                .questionId(answerOption.getQuestion() != null ? answerOption.getQuestion().getId() : null)
                .build();
    }

    private AnswerOption mapToEntity(AnswerOptionDTO.Request request, Question question) {
        return AnswerOption.builder()
                .question(question)
                .optionLabel(request.optionLabel())
                .optionText(request.optionText())
                .isCorrect(request.isCorrect())
                .build();
    }

    @Override
    public AnswerOptionDTO.Response create(AnswerOptionDTO.Request request) {
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + request.questionId()));

        AnswerOption answerOption = mapToEntity(request, question);
        AnswerOption saved = answerOptionRepository.save(answerOption);
        return mapToDto(saved);
    }

    @Override
    public AnswerOptionDTO.Response update(Long id, AnswerOptionDTO.Request request) {
        AnswerOption existingOption = answerOptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer Option not found with ID: " + id));

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + request.questionId()));

        existingOption.setQuestion(question);
        existingOption.setOptionLabel(request.optionLabel());
        existingOption.setOptionText(request.optionText());
        existingOption.setIsCorrect(request.isCorrect());

        AnswerOption updated = answerOptionRepository.save(existingOption);
        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!answerOptionRepository.existsById(id)) {
            throw new EntityNotFoundException("Answer Option not found with ID: " + id);
        }
        answerOptionRepository.deleteById(id);
    }

    @Override
    public AnswerOptionDTO.Response findById(Long id) {
        return answerOptionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Answer Option not found with ID: " + id));
    }

    @Override
    public List<AnswerOptionDTO.Response> findAllByQuestionId(Long questionId) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));

        return answerOptionRepository.findByQuestionId(questionId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}