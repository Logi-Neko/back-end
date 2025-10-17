package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ContestDTO;
import exe2.learningapp.logineko.quizziz.dto.ContestHistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ContestService {
 ContestDTO.ContestResponse create(ContestDTO.ContestRequest create);
 ContestDTO.UpdateRoom update(Long id, ContestDTO.UpdateRoom update);
 void delete(Long id);
 Optional<ContestDTO.ContestResponse> findById(Long id);
 Page<ContestDTO.ContestResponse> findAll(String keyword , Pageable pageable);
 void startContest(Long id);
 void endContest(Long id);
 void rewardTopFiveParticipants(Long contestId);
 List<ContestHistoryDTO> getContestHistory(Long accountId);
}
