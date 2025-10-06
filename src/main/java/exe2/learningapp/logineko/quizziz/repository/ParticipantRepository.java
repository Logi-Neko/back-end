package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository  extends JpaRepository<Participant,Long> {
    @Modifying
    @Query("UPDATE Participant p SET p.score = p.score + :delta WHERE p.id = :participantId")
    int incrementScoreById(Long participantId, int delta);
    
    Optional<Participant> findByContest_IdAndAccount_Id(Long contestId, Long accountId);
    List<Participant> findByContest_Id(Long contestId);
}
