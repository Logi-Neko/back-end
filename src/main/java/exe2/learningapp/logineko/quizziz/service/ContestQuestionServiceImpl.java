package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ContestQuestionDTO;
import exe2.learningapp.logineko.quizziz.entity.Contest;
import exe2.learningapp.logineko.quizziz.entity.ContestQuestion;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.repository.ContestQuestionRepository;
import exe2.learningapp.logineko.quizziz.repository.ContestRepository;
import exe2.learningapp.logineko.quizziz.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestQuestionServiceImpl implements ContestQuestionService{
    private final ContestQuestionRepository contestQuestionRepository;
    private final ContestRepository contestRepository;
    private final QuestionRepository questionRepository;
    @Override
    public ContestQuestionDTO.ContestQuestionResponse addQuestionToContest(ContestQuestionDTO.ContestQuestionRequest contestQuestionRequest) {
        Contest contest = contestRepository.findById(contestQuestionRequest.contestId())
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        Question question = questionRepository.findById(contestQuestionRequest.questionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        ContestQuestion entity = ContestQuestion.builder()
                .contest(contest)
                .question(question)
                .index(contestQuestionRequest.index())
                .build();

        ContestQuestion saved = contestQuestionRepository.save(entity);

        return ContestQuestionDTO.ContestQuestionResponse.builder()
                .id(saved.getId())
                .contestId(contest.getId())
                .questionId(question.getId())
                .index(saved.getIndex())
                .build();
    }

    @Override
    public ContestQuestionDTO.ContestQuestionResponse update(Long id, ContestQuestionDTO.ContestQuestionRequest contestQuestionRequest) {
        ContestQuestion entity = contestQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ContestQuestion not found"));

        Contest contest = contestRepository.findById(contestQuestionRequest.contestId())
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        Question question = questionRepository.findById(contestQuestionRequest.questionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        entity.setContest(contest);
        entity.setQuestion(question);
        entity.setIndex(contestQuestionRequest.index());

        ContestQuestion saved = contestQuestionRepository.save(entity);

        return ContestQuestionDTO.ContestQuestionResponse.builder()
                .id(saved.getId())
                .contestId(contest.getId())
                .questionId(question.getId())
                .index(saved.getIndex())
                .build();
    }

    @Override
    public void delete(Long id) {
        contestQuestionRepository.deleteById(id);
    }

    @Override
    public Optional<ContestQuestionDTO.ContestQuestionResponse> findById(Long id) {
        return contestQuestionRepository.findById(id)
                .map(entity -> ContestQuestionDTO.ContestQuestionResponse.builder()
                        .id(entity.getId())
                        .contestId(entity.getContest().getId())
                        .questionId(entity.getQuestion().getId())
                        .index(entity.getIndex())
                        .build());
    }

    @Override
    public Page<ContestQuestionDTO.ContestQuestionResponse> findByContest(Long contestId, Pageable pageable) {
        return contestQuestionRepository.findByContest_Id(contestId,pageable)
                .map(entity -> ContestQuestionDTO.ContestQuestionResponse.builder()
                        .id(entity.getId())
                        .contestId(entity.getContest().getId())
                        .questionId(entity.getQuestion().getId())
                        .index(entity.getIndex())
                        .build());
    }
}

