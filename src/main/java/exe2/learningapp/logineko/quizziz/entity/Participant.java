package exe2.learningapp.logineko.quizziz.entity;

import exe2.learningapp.logineko.authentication.entity.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "participant")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Column(name ="join_at", nullable = false)
    private LocalDateTime joinAt;
}
