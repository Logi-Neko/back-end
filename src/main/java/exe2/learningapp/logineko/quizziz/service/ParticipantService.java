package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ParticipantDTO;

import java.util.Optional;

public interface ParticipantService {
    Optional<ParticipantDTO.Response> findById(Long id);
    int incrementScore(Long participantId, int delta);

}
