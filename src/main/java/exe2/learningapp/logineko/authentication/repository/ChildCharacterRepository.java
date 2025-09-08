package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.ChildCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildCharacterRepository extends JpaRepository<ChildCharacter, Long>
{
}
