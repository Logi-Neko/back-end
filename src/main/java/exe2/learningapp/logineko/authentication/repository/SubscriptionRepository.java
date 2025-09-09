package exe2.learningapp.logineko.authentication.repository;

import exe2.learningapp.logineko.authentication.entity.Subscription;
import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Tìm theo account ID
    List<Subscription> findByAccountId(Long accountId);

    // Tìm theo account ID và trạng thái
    List<Subscription> findByAccountIdAndSubscriptionStatus(Long accountId, SubscriptionStatus status);

    // Tìm theo trạng thái
    List<Subscription> findBySubscriptionStatus(SubscriptionStatus status);

    // Tìm theo loại subscription
    List<Subscription> findByType(String type);

    // Tìm theo khoảng thời gian kết thúc
    List<Subscription> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    // Tìm theo thời gian tạo
    List<Subscription> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Tìm subscription hết hạn
    List<Subscription> findBySubscriptionStatusAndEndDateBefore(SubscriptionStatus status, LocalDate date);

    // Đếm theo trạng thái
    long countBySubscriptionStatus(SubscriptionStatus status);

    // Đếm subscription active và chưa hết hạn
    long countBySubscriptionStatusAndEndDateAfter(SubscriptionStatus status, LocalDate date);

    // Xóa theo account ID
    void deleteByAccountId(Long accountId);

    // Tìm subscription active của một account
    List<Subscription> findByAccountIdAndSubscriptionStatusAndEndDateAfter(
            Long accountId, SubscriptionStatus status, LocalDate currentDate);

    // Kiểm tra tồn tại subscription active
    boolean existsByAccountIdAndSubscriptionStatusAndEndDateAfter(
            Long accountId, SubscriptionStatus status, LocalDate currentDate);

    // Tìm subscription sắp hết hạn
    List<Subscription> findBySubscriptionStatusAndEndDateBetween(
            SubscriptionStatus status, LocalDate startDate, LocalDate endDate);

    // Tìm subscription theo account email thông qua account relationship
    List<Subscription> findByAccountEmail(String email);

    // Lấy subscription mới nhất của account
    List<Subscription> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    // Lấy subscription của account theo type
    List<Subscription> findByAccountIdAndType(Long accountId, String type);

    // Tìm subscription còn hiệu lực
    List<Subscription> findBySubscriptionStatusAndEndDateAfter(SubscriptionStatus status, LocalDate date);

    // Đếm subscription theo type
    long countByType(String type);

    // Tìm subscription được tạo trong khoảng thời gian
    List<Subscription> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // Kiểm tra subscription active theo ID
    boolean existsByIdAndSubscriptionStatusAndEndDateAfter(Long id, SubscriptionStatus status, LocalDate date);
}