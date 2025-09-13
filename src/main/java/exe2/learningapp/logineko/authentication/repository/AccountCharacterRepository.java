package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.AccountCharacter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountCharacterRepository extends JpaRepository<AccountCharacter, Long> , JpaSpecificationExecutor<AccountCharacter> {

    List<AccountCharacter> findByAccountId(Long accountId);

    List<AccountCharacter> findByAccountIdAndIsFavoriteTrue(Long accountId);

    Optional<AccountCharacter> findByAccountIdAndCharacterId(Long accountId, Long characterId);

}