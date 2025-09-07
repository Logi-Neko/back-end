package exe2.learningapp.logineko.authentication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "child_characters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JoinColumn(name = "child_id")
    @ManyToOne
    Child child;
    @JoinColumn(name = "character_id")
    @ManyToOne
    Character character;
    LocalDateTime unlockedAt;
    boolean isFavorite;
}
