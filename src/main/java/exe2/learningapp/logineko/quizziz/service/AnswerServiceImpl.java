package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerDTO;
import exe2.learningapp.logineko.quizziz.entity.Answer;
import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.repository.AnswerOptionRepository;
import exe2.learningapp.logineko.quizziz.repository.AnswerRepository;
import exe2.learningapp.logineko.quizziz.repository.ParticipantRepository;
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
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final ParticipantRepository participantRepository;

    private AnswerDTO.Response mapToDto(Answer answer) {
        return AnswerDTO.Response.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .isCorrect(answer.isCorrect())
                .answerTime(answer.getAnswerTime())
                .score(answer.getScore())
                .participantId(answer.getParticipant() != null ? answer.getParticipant().getId() : null)
                .questionId(answer.getQuestion() != null ? answer.getQuestion().getId() : null)
                .selectedOptionId(answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null)
                .build();
    }

    @Override
    public AnswerDTO.Response submitAnswer(AnswerDTO.CreateRequest request) {
        Participant participant = participantRepository.findById(request.participantId())
                .orElseThrow(() -> new EntityNotFoundException("Participant not found with ID: " + request.participantId()));

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + request.questionId()));

        AnswerOption selectedOption = answerOptionRepository.findById(request.selectedOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Answer Option not found with ID: " + request.selectedOptionId()));

        if (!selectedOption.getQuestion().getId().equals(question.getId())) {
            throw new IllegalArgumentException("Selected answer option does not belong to the provided question.");
        }

        boolean isCorrect = selectedOption.getIsCorrect();
        int score = 0;
        if (isCorrect) {
            int timeLimit = question.getTimeLimit();
            int maxPoints = question.getPoints();
            double timeRatio = (double) request.answerTime() / timeLimit;
            timeRatio = Math.min(timeRatio, 1.0);
            score = (int) Math.round((1 - timeRatio) * maxPoints);
        }

        Answer answer = Answer.builder()
                .participant(participant)
                .question(question)
                .selectedOption(selectedOption)
                .answerText(selectedOption.getOptionText())
                .isCorrect(isCorrect)
                .answerTime(request.answerTime())
                .score(score)
                .build();

        Answer savedAnswer = answerRepository.save(answer);
        return mapToDto(savedAnswer);
    }

    @Override
    public AnswerDTO.Response findById(Long id) {
        return answerRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with ID: " + id));
    }

    @Override
    public List<AnswerDTO.Response> findAllByQuestionId(Long questionId) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found with ID: " + questionId));

        return answerRepository.findByQuestionId(questionId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AnswerDTO.Response> findAllByParticipantId(Long participantId, Pageable pageable) {
        participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found with ID: " + participantId));

        return answerRepository.findByParticipantId(participantId, pageable)
                .map(this::mapToDto);
    }
}
