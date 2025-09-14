package exe2.learningapp.logineko.lesson.entities;

import exe2.learningapp.logineko.authentication.entity.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_question_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountQuestionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    Video video;

    @Column(nullable = false)
    Boolean isCorrect;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    LocalDateTime createdAt;
}
