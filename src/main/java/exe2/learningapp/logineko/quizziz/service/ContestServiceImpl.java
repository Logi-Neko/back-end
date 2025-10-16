package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.quizziz.dto.ContestDTO;
import exe2.learningapp.logineko.quizziz.dto.ContestHistoryDTO;
import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import exe2.learningapp.logineko.quizziz.entity.Contest;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.repository.ContestRepository;
import exe2.learningapp.logineko.quizziz.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {
    private final ContestRepository contestRepository;
    private final ParticipantRepository participantRepository;
    private final LeaderBoardService leaderBoardService;
    private final AccountRepository accountRepository;


    @Override
    public ContestDTO.ContestResponse create(ContestDTO.ContestRequest create) {
        Contest room = new Contest();
        room.setTitle(create.title());
        room.setDescription(create.description());
        room.setStartTime(create.startTime() != null ? create.startTime() : LocalDateTime.now() );
        room.setEndTime(LocalDateTime.now().plusHours(1));
        String generatedCode;
        do {
            generatedCode = generateUniqueRoomCode();
        } while (contestRepository.findByCode(generatedCode).isPresent());

        room.setCode(generatedCode);


        Contest saved = contestRepository.save(room);

        return new ContestDTO.ContestResponse(
                saved.getId(),
                saved.getCode(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus().toString(),
                saved.getStartTime()
//                saved.getEndTime(),
//                saved.getCreatorId()
        );
        }

    @Override
    public ContestDTO.UpdateRoom update(Long id, ContestDTO.UpdateRoom update) {
        Contest room = contestRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        room.setTitle(update.title());
        room.setDescription(update.description());

        Contest saved = contestRepository.save(room);

        return new ContestDTO.UpdateRoom(
                saved.getTitle(),
                saved.getDescription()
        );


    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Optional<ContestDTO.ContestResponse> findById(Long id) {
        return contestRepository.findById(id)
                .map(r -> new ContestDTO.ContestResponse(
                        r.getId(),
                        r.getCode(),
                        r.getTitle(),
                        r.getDescription(),
                        r.getStatus().toString(),
                        r.getStartTime()
                ));
    }

    @Override
    public Page<ContestDTO.ContestResponse> findAll(String keyword, Pageable pageable) {
        Page<Contest> rooms;
        if (keyword == null || keyword.isBlank()) {
            rooms = contestRepository.findAll(pageable);
        } else {
            rooms = contestRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable
            );
        }

        return rooms.map(r -> new ContestDTO.ContestResponse(
                r.getId(),
                r.getCode(),
                r.getTitle(),
                r.getDescription(),
                r.getStatus().toString(),
                r.getStartTime()

        ));
    }

    private String generateUniqueRoomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder codeBuilder = new StringBuilder();

        codeBuilder.append(characters.charAt(new Random().nextInt(characters.length())));
        codeBuilder.append(characters.charAt(new Random().nextInt(characters.length())));


        String numbers = "0123456789";
        for (int i = 0; i < 4; i++) {
            codeBuilder.append(numbers.charAt(new Random().nextInt(numbers.length())));
        }

        return codeBuilder.toString();
    }

    @Override
    @Transactional
    public void startContest(Long id) {
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        if (contest.getStatus() != Contest.Status.OPEN) {
            throw new IllegalStateException("Contest is not in OPEN state");
        }

        contest.setStatus(Contest.Status.RUNNING);
        contest.setStartTime(LocalDateTime.now());
        contestRepository.save(contest);
        
        // Note: Event publishing is now handled in EventProducer.publishContestStarted()
        // to avoid circular dependency
    }

    @Override
    @Transactional
    public void endContest(Long id) {
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        if (contest.getStatus() != Contest.Status.RUNNING) {
            throw new IllegalStateException("Contest is not in RUNNING state");
        }

        contest.setStatus(Contest.Status.CLOSED);
        contest.setEndTime(LocalDateTime.now());
        contestRepository.save(contest);
        
        // Note: Event publishing is now handled in EventProducer.publishContestEnded()
        // to avoid circular dependency
    }
    @Override
    @Transactional
    public void rewardTopFiveParticipants(Long contestId) {
        List<LeaderBoardDTO.LeaderBoardResponse> leaderboard = leaderBoardService.getLeaderboard(contestId);
        int limit = Math.min(5, leaderboard.size());
        for (int i = 0; i < limit; i++) {
            LeaderBoardDTO.LeaderBoardResponse boardResponse = leaderboard.get(i);
            Participant participant = participantRepository.findById(boardResponse.participantId())
                    .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));
            Account account = participant.getAccount();
            // Example: Add 100 stars for top 5
            account.setTotalStar(account.getTotalStar() + 100 - i*20);
            accountRepository.save(account);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestHistoryDTO> getContestHistory(Long accountId) {
        List<Participant> participations = participantRepository.findByAccount_Id(accountId);
        if (participations.isEmpty()) {
            return new ArrayList<>();
        }
        return participations.stream().map(participant -> {
            Contest contest = participant.getContest();
            int rank = leaderBoardService.computeRank(contest.getId(), participant.getId());
            return ContestHistoryDTO.builder()
                    .contestId(contest.getId())
                    .contestTitle(contest.getTitle())
                    .startTime(contest.getStartTime())
                    .score(participant.getScore())
                    .rank(rank)
                    .build();
        }).collect(Collectors.toList());
    }
}
