package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ContestQuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContestQuestionService {
    ContestQuestionDTO.Response  addQuestionToContest(ContestQuestionDTO.Request create);
    void delete(Long id);
   Optional< ContestQuestionDTO.Response> findById(Long id);
    ContestQuestionDTO.Response update(Long id, ContestQuestionDTO.Request request);
    Page<ContestQuestionDTO.Response> findByContest (Long contestId, Pageable pageable);


}
