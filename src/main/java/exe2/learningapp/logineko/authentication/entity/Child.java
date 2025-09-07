package exe2.learningapp.logineko.authentication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Entity
@Table(name = "children")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    LocalDate birthDate;
    String gender;
    String imageUrl;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    Account parent;

}
