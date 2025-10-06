package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminStatDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MonthData {
        Long month;
        Long revenue;
        Long newUsers;
        Long newPremiumUsers;
        Double monthOverMonthGrowth;
    }

    Long totalUsers;
    Long totalPremiumUsers;
    Long totalRevenue;
    Long totalQuestions;
    Long year;
    Long totalRevenueInYear;
    Double averageRevenueInMonth;
    Long monthWithHighestRevenue;
    Double yearOverYearGrowth;
    List<MonthData> monthData;
}
