package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantRepository  extends JpaRepository<Participant,Long> {
    @Modifying
    @Query("UPDATE Participant p SET p.score = p.score + :delta WHERE p.id = :participantId")
    int incrementScoreById(Long participantId, int delta);
}
