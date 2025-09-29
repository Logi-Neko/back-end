package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ParticipantDTO;
import exe2.learningapp.logineko.quizziz.entity.Participant;

import java.util.Optional;

public interface ParticipantService {
    Optional<ParticipantDTO.ParticipantResponse> findById(Long id);
    int incrementScore(Long participantId, int delta);
    Participant createParticipant(Long contestId, Long accountId);
    Optional<Participant> findParticipantEntity(Long id);
}
