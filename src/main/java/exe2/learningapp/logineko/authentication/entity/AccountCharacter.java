package exe2.learningapp.logineko.authentication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "account_characters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JoinColumn(name = "account_id")
    @ManyToOne
    Account account;
    @JoinColumn(name = "character_id")
    @ManyToOne
    Character character;
    @CreationTimestamp
    LocalDateTime unlockedAt;
    boolean isFavorite;
}
