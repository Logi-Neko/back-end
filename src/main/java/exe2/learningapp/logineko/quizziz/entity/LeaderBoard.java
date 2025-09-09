package exe2.learningapp.logineko.quizziz.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "leaderboard")
public class LeaderBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Integer totalScore;
    private Integer rank;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;



    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

}
