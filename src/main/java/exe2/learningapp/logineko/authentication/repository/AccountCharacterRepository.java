package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.AccountCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountCharacterRepository extends JpaRepository<AccountCharacter, Long> {

    List<AccountCharacter> findByAccountId(Long accountId);

    List<AccountCharacter> findByAccountIdAndIsFavoriteTrue(Long accountId);

    Optional<AccountCharacter> findByAccountIdAndCharacterId(Long accountId, Long characterId);

    boolean existsByAccountIdAndCharacterId(Long accountId, Long characterId);

    long countByAccountId(Long accountId);

    long countByAccountIdAndIsFavoriteTrue(Long accountId);

}