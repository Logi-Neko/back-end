package exe2.learningapp.logineko.quizziz.entity;

import exe2.learningapp.logineko.authentication.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "contest")
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

//    @Column(name = "total_questions", nullable = false)
//    private int totalQuestions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = true)
    private Account creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Contest.Status status = Contest.Status.OPEN;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL)
    private List<ContestQuestion> contestQuestion;

    @Column(name="start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name="end_time")
    private LocalDateTime endTime;
    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public  enum Status {
        OPEN,CLOSED,
        RUNNING
    }
}
