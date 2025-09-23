package exe2.learningapp.logineko.quizziz.service;

import exe2.learningapp.logineko.quizziz.dto.LeaderBoardDTO;

import java.util.List;

public interface LeaderBoardService {
    void updateScore(Long contestId, Long participantId, int delta);
    List<LeaderBoardDTO.LeaderBoardResponse> finalizeLeaderboard(Long contestId);
    List<LeaderBoardDTO.LeaderBoardResponse> getLeaderboard(Long contestId);
    int computeRank(Long contestId, Long participantId);

}
