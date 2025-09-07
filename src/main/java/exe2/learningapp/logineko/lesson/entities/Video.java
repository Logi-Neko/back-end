package exe2.learningapp.logineko.lesson.entities;

import exe2.learningapp.logineko.lesson.entities.enums.VideoType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 50)
    String title;

    @Column(nullable = false, length = 500)
    String videoUrl;

    @Column(nullable = false)
    String videoPublicId;

    @Column(length = 500)
    String thumbnailUrl;

    @Column(nullable = false)
    String thumbnailPublicId;

    @Column(nullable = false)
    Long duration;

    @Column(nullable = false)
    Long order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    VideoType type;

    @Column(nullable = false)
    Boolean isActive;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;
}
