package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.AnswerOptionDTO;
import exe2.learningapp.logineko.quizziz.entity.AnswerOption;
import exe2.learningapp.logineko.quizziz.entity.Question;
import exe2.learningapp.logineko.quizziz.repository.AnswerOptionRepository;
import exe2.learningapp.logineko.quizziz.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerOptionServiceImpl implements AnswerOptionService {

    private final AnswerOptionRepository answerOptionRepository;
    private final QuestionRepository questionRepository;

    @Override
    public AnswerOptionDTO.AnswerOptionResponse create(AnswerOptionDTO.AnswerOptionRequest request) {
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        AnswerOption option = AnswerOption.builder()
                .question(question)
                .optionText(request.optionText())
                .isCorrect(request.isCorrect())
                .build();

        AnswerOption saved = answerOptionRepository.save(option);

        return AnswerOptionDTO.AnswerOptionResponse.builder()
                .id(saved.getId())
                .optionText(saved.getOptionText())
                .isCorrect(saved.getIsCorrect())
                .questionId(saved.getQuestion().getId())
                .build();
    }

    @Override
    public AnswerOptionDTO.AnswerOptionResponse update(Long id, AnswerOptionDTO.AnswerOptionRequest request) {
        AnswerOption option = answerOptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AnswerOption not found"));

        option.setOptionText(request.optionText());
        option.setIsCorrect(request.isCorrect());

        AnswerOption saved = answerOptionRepository.save(option);

        return AnswerOptionDTO.AnswerOptionResponse.builder()
                .id(saved.getId())
                .optionText(saved.getOptionText())
                .isCorrect(saved.getIsCorrect())
                .questionId(saved.getQuestion().getId())
                .build();
    }

    @Override
    public void delete(Long id) {
        if(!answerOptionRepository.existsById(id)) {
            throw new RuntimeException("AnswerOption not found");
        }
        answerOptionRepository.deleteById(id);
    }

    @Override
    public Optional<AnswerOptionDTO.AnswerOptionResponse> findById(Long id) {
        return answerOptionRepository.findById(id)
                .map(opt -> AnswerOptionDTO.AnswerOptionResponse.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .isCorrect(opt.getIsCorrect())
                        .questionId(opt.getQuestion().getId())
                        .build())
                ;
    }

    @Override
    public List<AnswerOptionDTO.AnswerOptionResponse> findByQuestion(Long questionId) {
        return answerOptionRepository.findByQuestion_Id(questionId).stream()
                .map(opt -> AnswerOptionDTO.AnswerOptionResponse.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .isCorrect(opt.getIsCorrect())
                        .questionId(opt.getQuestion().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
