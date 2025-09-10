package exe2.learningapp.logineko.authentication.entity;

import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JoinColumn(name = "account_id")
    @ManyToOne
    Account account;

    String type;
    @JoinColumn(name = "start_date")
    LocalDate startDate;
    @JoinColumn(name = "end_date")
    LocalDate endDate;

    double price;
    @JoinColumn(name = "subscription_status")
    @Enumerated(EnumType.STRING)
    SubscriptionStatus subscriptionStatus;
    @CreationTimestamp
    LocalDateTime createdAt;
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
