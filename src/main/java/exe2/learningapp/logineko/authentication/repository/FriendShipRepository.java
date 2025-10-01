package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

    // Check if friendship exists between two accounts with PENDING status
    Optional<FriendShip> findByFromAccountAndToAccountAndStatus(Account fromAccount, Account toAccount, FriendShip.StatusFriendShip status);

    // Get all accepted friendships for an account (both sent and received)
    @Query("SELECT f FROM FriendShip f WHERE (f.fromAccount = :account OR f.toAccount = :account) AND f.status = 'ACCEPTED'")
    List<FriendShip> findAcceptedFriendshipsByAccount(@Param("account") Account account);

    List<FriendShip> findByToAccountAndStatus(Account toAccount, FriendShip.StatusFriendShip status);
}
