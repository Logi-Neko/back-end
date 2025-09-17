package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.ParticipantDTO;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService{
    private final ParticipantRepository participantRepository;
    @Override
    public Optional<ParticipantDTO.ParticipantResponse> findById(Long id) {
        return participantRepository.findById(id).map(participant -> ParticipantDTO.ParticipantResponse.builder()
                .id(participant.getId())
                .build());
    }

    @Override
    public int incrementScore(Long participantId, int delta) {
        int updatedRows = participantRepository.incrementScoreById(participantId, delta);
        if (updatedRows == 0) {
            throw new RuntimeException("Unable to increment score for participant " + participantId);
        }
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found: " + participantId));
        return participant.getScore();
    }

}
