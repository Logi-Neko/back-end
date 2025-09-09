package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.ChildCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildCharacterRepository extends JpaRepository<ChildCharacter, Long>
{
    List<ChildCharacter> findByChild_Id(Long childId);
    List<ChildCharacter> findByChild_IdAndIsFavoriteTrue(Long childId);
    Optional<ChildCharacter> findByChild_IdAndCharacter_Id(Long childId, Long characterId);
    int deleteByChild_Id(Long childId);
}
