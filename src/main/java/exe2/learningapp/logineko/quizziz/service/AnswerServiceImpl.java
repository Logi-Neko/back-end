package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerDTO;
import exe2.learningapp.logineko.quizziz.entity.*;
import exe2.learningapp.logineko.quizziz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final ParticipantRepository participantRepository;
    private final ContestQuestionRepository contestQuestionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    private AnswerDTO.AnswerResponse mapToDto(Answer answer) {
        return AnswerDTO.AnswerResponse.builder()
                .id(answer.getId())
                .isCorrect(answer.isCorrect())
                .answerTime(answer.getAnswerTime())
                .score(answer.getScore())
                .participantId(answer.getParticipant().getId())
                .questionId(answer.getContestQuestion().getId())
                .answerOptionId(answer.getSelectedOption().getId())
                .build();
    }

    @Override
    public AnswerDTO.AnswerResponse create(AnswerDTO.AnswerRequest request) {
        Participant participant = participantRepository.findById(request.participantId())
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        ContestQuestion contestQuestion = contestQuestionRepository.findById(request.contestQuestionId())
                .orElseThrow(() -> new RuntimeException("ContestQuestion not found"));

        AnswerOption option = answerOptionRepository.findById(request.answerOptionId())
                .orElseThrow(() -> new RuntimeException("AnswerOption not found"));

        // check correctness
        boolean isCorrect = option.getIsCorrect();
        int baseScore = isCorrect ? 1000 : 0;

        // score can depend on speed, ví dụ: max 1000, giảm theo thời gian
        int score = isCorrect ? Math.max(0, baseScore - request.answerTime() * 10) : 0;

        Answer answer = Answer.builder()
                .participant(participant)
                .contestQuestion(contestQuestion)
                .selectedOption(option)
                .isCorrect(isCorrect)
                .answerTime(request.answerTime())
                .score(score)
                .build();

        return mapToDto(answerRepository.save(answer));
    }

    @Override
    public void delete(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new RuntimeException("Answer not found");
        }
        answerRepository.deleteById(id);
    }

    @Override
    public Optional<AnswerDTO.AnswerResponse> findById(Long id) {
        return answerRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public Page<AnswerDTO.AnswerResponse> findByParticipant(Long participantId, Pageable pageable) {
        return answerRepository.findByParticipantId(participantId, pageable).map(this::mapToDto);
    }

    @Override
    public Page<AnswerDTO.AnswerResponse> findByContestQuestion(Long contestQuestionId, Pageable pageable) {
        return answerRepository.findByContestQuestionId(contestQuestionId, pageable).map(this::mapToDto);
    }


    @Override
    public boolean existsBySubmissionUuid(Long submissionUuid) {
        if (submissionUuid == null) return false;
        return answerRepository.existsBySubmissionUuid(submissionUuid);
    }

    /**
     * Lưu Answer dựa trên event (kèm submissionUuid) — idempotent.
     * - Kiểm tra tồn tại submissionUuid trước khi lưu.
     * - Gọi trong transactional context từ consumer.
     */
    @Override
    @Transactional
    public void saveFromEvent(
            Long submissionUuid,
            Long participantId,
            Long contestQuestionId,
            Long answerOptionId,
            boolean isCorrect,
            int score,
            int answerTime
    ) {
        if (submissionUuid != null && answerRepository.existsBySubmissionUuid(submissionUuid)) {
            return;
        }

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found: " + participantId));

        ContestQuestion contestQuestion = contestQuestionRepository.findById(contestQuestionId)
                .orElseThrow(() -> new RuntimeException("ContestQuestion not found: " + contestQuestionId));

        AnswerOption selectedOption = answerOptionRepository.findById(answerOptionId)
                .orElseThrow(() -> new RuntimeException("AnswerOption not found: " + answerOptionId));

        Answer answer = Answer.builder()
                .submissionUuid(submissionUuid)
                .participant(participant)
                .contestQuestion(contestQuestion)
                .selectedOption(selectedOption)
                .isCorrect(isCorrect)
                .answerTime(answerTime)
                .score(score)
                .build();

        answerRepository.save(answer);
        // Note: do not update participant score here — caller (GameProcessor) will call participantService.incrementScore
    }

    @Override
    public void saveAnswer(AnswerDTO.AnswerResponse answerResponse) {
        // Implementation not needed for now
    }

    @Override
    @Transactional
    public void updateAnswerScore(Long answerId, boolean isCorrect, int score) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found with ID: " + answerId));
        
        answer.setCorrect(isCorrect);
        answer.setScore(score);
        answerRepository.save(answer);
    }
}
