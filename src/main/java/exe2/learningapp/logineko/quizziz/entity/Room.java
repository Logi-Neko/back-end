package exe2.learningapp.logineko.quizziz.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;



}
