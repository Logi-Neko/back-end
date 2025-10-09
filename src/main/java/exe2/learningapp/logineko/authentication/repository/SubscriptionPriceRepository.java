package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.SubscriptionPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPriceRepository extends JpaRepository<SubscriptionPrice, Long> {
}
