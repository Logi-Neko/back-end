package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;

import java.util.List;

public interface LeaderBoardService {
    void updateScore(Long contestId, Long participantId, int delta);
    void finalizeLeaderboard(Long contestId);
    List<LeaderBoardDTO.Response> getLeaderboard(Long contestId);
    int computeRank(Long contestId, Long participantId);

}
