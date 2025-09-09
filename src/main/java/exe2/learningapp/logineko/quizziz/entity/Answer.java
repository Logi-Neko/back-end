package exe2.learningapp.logineko.quizziz.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_option_id", nullable = false)
    private AnswerOption selectedOption;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name="answer_time", nullable = false)
    private int answerTime; // in seconds

    @Column(name="score", nullable = false)
    private int score;
}
