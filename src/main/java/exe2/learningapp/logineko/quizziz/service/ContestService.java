package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ContestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContestService {
 ContestDTO.Response create(ContestDTO.Request create);
 ContestDTO.UpdateRoom update(Long id, ContestDTO.UpdateRoom update);
 void delete(Long id);
 Optional<ContestDTO.Response> findById(Long id);
 Page<ContestDTO.Response> findAll(String keyword , Pageable pageable);
}
