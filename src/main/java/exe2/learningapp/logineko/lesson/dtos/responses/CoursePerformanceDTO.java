package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoursePerformanceDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CourseStats {
        Long courseId;
        String courseName;
        String description;
        String thumbnailUrl;
        Boolean isPremium;
        Long totalLessons;
        Long totalVideos;
        Long totalQuestions;
        Long uniqueStudents;
        Long totalAttempts;
        Double averageScore;
        Double completionRate;
        Long price;
    }

    List<CourseStats> popularCourses;
    Long totalCourses;
    Long totalPremiumCourses;
    Long totalFreeCourses;
    Long activeCourses;
    Double averageStudentsPerCourse;
}
