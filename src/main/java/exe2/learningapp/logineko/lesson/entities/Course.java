package exe2.learningapp.logineko.lesson.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String description;

    @Column(length = 500)
    String thumbnailUrl;

    String thumbnailPublicId;

    @Column(nullable = false)
    Long totalLesson;

    @Column(nullable = false)
    Boolean isPremium;

    @Column(nullable = false)
    Boolean isActive;

    @Column(nullable = false)
    Long price;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    LocalDateTime updatedAt;
}
