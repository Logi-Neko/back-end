package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import exe2.learningapp.logineko.quizziz.entity.LeaderBoard;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.repository.LeaderBoardRepository;
import exe2.learningapp.logineko.quizziz.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderBoardServiceImpl implements  LeaderBoardService{
    private final LeaderBoardRepository leaderboardRepository;
    private final ParticipantRepository participantRepository;
    @Transactional
    @Override
    public void updateScore(Long contestId, Long participantId, int delta) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));
        
        // Calculate total score from all answers for this participant
        int totalScore = participant.getSubmissions().stream()
                .mapToInt(answer -> answer.getScore())
                .sum();
        
        LeaderBoard lb = leaderboardRepository
                .findByContest_IdAndParticipant_Id(contestId, participant.getId());

        if (lb == null) {
            lb = LeaderBoard.builder()
                    .contest(participant.getContest())
                    .participant(participant)
                    .score(totalScore)
                    .build();
        } else {
            lb.setScore(lb.getScore()+totalScore);
        }
        leaderboardRepository.save(lb);
        
        // Also update participant's total score
        participant.setScore(participant.getScore()+totalScore);
        participantRepository.save(participant);
        
        log.info("📊 Updated leaderboard for participant {} in contest {}: {} points", 
            participantId, contestId, totalScore);
    }
    @Transactional
    @Override
    public List<LeaderBoardDTO.LeaderBoardResponse> finalizeLeaderboard(Long contestId) {
        List<LeaderBoard> boards = leaderboardRepository.findByContest_IdOrderByScoreDesc(contestId);
        for (int i = 0; i < boards.size(); i++) {
            boards.get(i).setFinalRank(i + 1);
        }
        leaderboardRepository.saveAll(boards);
        return IntStream.range(0, boards.size())
                .mapToObj(i -> {
                    LeaderBoard lb = boards.get(i);
                    return LeaderBoardDTO.LeaderBoardResponse.builder()
                            .participantId(lb.getParticipant().getId())
                            .participantName(lb.getParticipant().getAccount().getFirstName())
                            .score(lb.getScore())
                            .rank(lb.getFinalRank())
                            .build();
                })
                .toList();
    }
    @Override
    public List<LeaderBoardDTO.LeaderBoardResponse> getLeaderboard(Long contestId) {
        List<LeaderBoard> boards = leaderboardRepository.findByContest_IdOrderByScoreDesc(contestId);
        return IntStream.range(0, boards.size())
                .mapToObj(i -> {
                    LeaderBoard lb = boards.get(i);
                    return LeaderBoardDTO.LeaderBoardResponse.builder()
                            .participantId(lb.getParticipant().getId())
                            .participantName(lb.getParticipant().getAccount().getFirstName())
                            .score(lb.getScore())
                            .rank(i + 1)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int computeRank(Long contestId, Long participantId) {
        List<LeaderBoard> leaderboardList = leaderboardRepository.findByContest_IdOrderByScoreDesc(contestId);

        // rank = vị trí trong list (score giảm dần)
        for (int i = 0; i < leaderboardList.size(); i++) {
            LeaderBoard lb = leaderboardList.get(i);
            if (lb.getParticipant().getId().equals(participantId)) {
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    @Transactional
    public void initializeLeaderboard(Long contestId) {
        // Get all participants for the contest
        List<Participant> participants = participantRepository.findByContest_Id(contestId);
        
        // Create leaderboard entries for each participant
        for (Participant participant : participants) {
            LeaderBoard existingEntry = leaderboardRepository
                    .findByContest_IdAndParticipant_Id(contestId, participant.getId());
            
            if (existingEntry == null) {
                LeaderBoard leaderBoard = LeaderBoard.builder()
                        .contest(participant.getContest())
                        .participant(participant)
                        .score(participant.getScore())
                        .build();
                leaderboardRepository.save(leaderBoard);
            }
        }
        
        log.info("Initialized leaderboard for contest {} with {} participants", contestId, participants.size());
    }
}
