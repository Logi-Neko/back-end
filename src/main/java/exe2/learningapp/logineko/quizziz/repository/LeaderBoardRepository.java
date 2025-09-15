package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.LeaderBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaderBoardRepository extends JpaRepository<LeaderBoard, Long> {
    List<LeaderBoard> findByContest_IdOrderByScoreDesc(Long contestId);
    LeaderBoard findByContest_IdAndParticipant_Id(Long contestId, Long participantId);

}
