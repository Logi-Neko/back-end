package exe2.learningapp.logineko.quizziz.repository;

import exe2.learningapp.logineko.quizziz.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository  extends JpaRepository<Participant,Long> {
}
