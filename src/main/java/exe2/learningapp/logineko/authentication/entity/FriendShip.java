package exe2.learningapp.logineko.authentication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "firendships")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendShip {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    Account toAccount;

    @Enumerated(EnumType.STRING)
    StatusFriendShip status;

    @CreationTimestamp
    LocalDateTime createdAt;
    @UpdateTimestamp
    LocalDateTime updatedAt;




    public enum StatusFriendShip {
        PENDING,
        ACCEPTED,
        REJECTED,
        BLOCKED,
        UNFRIENDED
    }
}
