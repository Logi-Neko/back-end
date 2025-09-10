package exe2.learningapp.logineko.lesson.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "video_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String question;

    @Column(nullable = false)
    String optionA;

    @Column(nullable = false)
    String optionB;

    @Column(nullable = false)
    String optionC;

    @Column(nullable = false)
    String optionD;

    @Column(nullable = false)
    String answer;
}
