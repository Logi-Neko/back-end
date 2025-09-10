package exe2.learningapp.logineko.quizziz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

//    @Column(name = "subject", nullable = false)
//    private String subject;

    @Column(name = "duration", nullable = false)
    private int duration;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Enumerated(EnumType.STRING)
    private  Status status = Status.OPEN;

    @Column(name="start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name="end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;


    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;


    public  enum Status {
      OPEN,CLOSED,
        RUNNING
    }
}