package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActiveUsersMetricsDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DailyActivity {
        LocalDate date;
        Long activeUsers;
        Long totalAttempts;
        Long totalQuestions;
    }

    LocalDate from;
    LocalDate to;
    Long totalActiveUsers;
    Long totalPremiumActiveUsers;
    Long totalFreeActiveUsers;
    Long dailyActiveUsers;
    Long weeklyActiveUsers;
    Long monthlyActiveUsers;
    Double averageAttemptsPerUser;
    List<DailyActivity> dailyActivities;
}
