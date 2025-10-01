package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUserId(String userId);

    // Find all accounts that have a specific role in their roles set
    @Query("SELECT a FROM Account a JOIN a.roles r WHERE r = :role")
    List<Account> findByRolesContaining(@Param("role") Role role);
}
