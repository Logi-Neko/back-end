package exe2.learningapp.logineko.quizziz.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz")
public class ContestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private int index;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    private Contest contest;

   @ManyToOne
   @JoinColumn(name="question_id", nullable = false)
   private Question question;


}