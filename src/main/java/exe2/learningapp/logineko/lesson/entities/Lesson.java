package exe2.learningapp.logineko.lesson.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Table
@Entity(name = "lessons")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, length = 100)
    String description;

    @Column(nullable = false)
    Long index;

    @Column(nullable = false)
    Long minAge;

    @Column(nullable = false)
    Long maxAge;

    @Column(nullable = false)
    Long difficultyLevel;

    @Column(length = 500, nullable = false)
    String thumbnailUrl;

    @Column(nullable = false)
    String thumbnailPublicId;

    @Column(nullable = false)
    Long duration;

    @Column(nullable = false)
    Boolean isPremium;

    @Column(nullable = false)
    Boolean isActive;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Video> videos;
}
