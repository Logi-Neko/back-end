package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ContestQuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContestQuestionService {
    ContestQuestionDTO.ContestQuestionResponse addQuestionToContest(ContestQuestionDTO.ContestQuestionRequest create);
    void delete(Long id);
   Optional<ContestQuestionDTO.ContestQuestionResponse> findById(Long id);
    ContestQuestionDTO.ContestQuestionResponse update(Long id, ContestQuestionDTO.ContestQuestionRequest contestQuestionRequest);
    List<ContestQuestionDTO.ContestQuestionResponse> findByContest (Long contestId);


}
