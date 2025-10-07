package exe2.learningapp.logineko.authentication.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "subscription_prices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "price")
    Long price;

    @Column(name = "duration")
    Long duration;
}
