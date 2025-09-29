package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.quizziz.dto.ParticipantDTO;
import exe2.learningapp.logineko.quizziz.entity.Contest;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.repository.ContestRepository;
import exe2.learningapp.logineko.quizziz.repository.ParticipantRepository;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService{
    private final ParticipantRepository participantRepository;
    private final ContestRepository contestRepository;
    private final AccountRepository accountRepository;
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

    @Override
    @Transactional
    public Participant createParticipant(Long contestId, Long accountId) {
        // Check if participant already exists
        Optional<Participant> existingParticipant = participantRepository.findByContest_IdAndAccount_Id(contestId, accountId);
        if (existingParticipant.isPresent()) {
            return existingParticipant.get();
        }

        // Get contest and account entities
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found: " + contestId));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        // Create new participant
        Participant participant = Participant.builder()
                .contest(contest)
                .account(account)
                .score(0)
                .joinAt(LocalDateTime.now())
                .build();

        return participantRepository.save(participant);
    }

    @Override
    public Optional<Participant> findParticipantEntity(Long id) {
        return participantRepository.findById(id);
    }

    @Override
    public List<ParticipantDTO.Participant> getParticipantsByContestId(Long contestId) {
        if(!contestRepository.existsById(contestId)) {
            throw new RuntimeException("Contest not found: " + contestId);
        }

        return participantRepository.findByContest_Id(contestId).stream()
                .map(p -> new ParticipantDTO.Participant(
                        p.getId(),
                        p.getAccount().getFirstName(),
                        p.getScore(),
                        p.getJoinAt()
                ))
                .toList();}
}
