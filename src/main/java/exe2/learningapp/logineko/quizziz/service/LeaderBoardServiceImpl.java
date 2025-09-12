package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;
import exe2.learningapp.logineko.quizziz.entity.LeaderBoard;
import exe2.learningapp.logineko.quizziz.entity.Participant;
import exe2.learningapp.logineko.quizziz.repository.LeaderBoardRepository;
import exe2.learningapp.logineko.quizziz.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LeaderBoardServiceImpl implements  LeaderBoardService{
    private final LeaderBoardRepository leaderboardRepository;
    private final ParticipantRepository participantRepository;
    @Transactional
    @Override
    public void updateScore(Long contestId, Long participantId, int delta) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));
        LeaderBoard lb = leaderboardRepository
                .findByContest_IdAndParticipant_Id(contestId, participant.getId());

        if (lb == null) {
            lb = LeaderBoard.builder()
                    .contest(participant.getContest())
                    .participant(participant)
                    .score(delta)
                    .build();
        } else {
            lb.setScore(lb.getScore() + delta);
        }
        leaderboardRepository.save(lb);
    }
    @Transactional
    @Override
    public void finalizeLeaderboard(Long contestId) {
        List<LeaderBoard> boards = leaderboardRepository.findByContest_IdOrderByScoreDesc(contestId);
        for (int i = 0; i < boards.size(); i++) {
            boards.get(i).setFinalRank(i + 1);
        }
        leaderboardRepository.saveAll(boards);
    }
    @Override
    public List<LeaderBoardDTO.Response> getLeaderboard(Long contestId) {
        List<LeaderBoard> boards = leaderboardRepository.findByContest_IdOrderByScoreDesc(contestId);
        return IntStream.range(0, boards.size())
                .mapToObj(i -> {
                    LeaderBoard lb = boards.get(i);
                    return LeaderBoardDTO.Response.builder()
                            .participantId(lb.getParticipant().getId())
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
}
