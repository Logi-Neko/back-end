package exe2.learningapp.logineko.lesson.services;

import exe2.learningapp.logineko.lesson.dtos.responses.*;

import java.time.LocalDate;

public interface StatisticService {
    StatisticDTO getStatistic(Long accountId, LocalDate from, LocalDate to);

    AdminStatDTO getAdminStat(long year);

    SubscriptionStatusBreakdownDTO getSubscriptionStatusBreakdown();

    ChurnRateDTO getChurnRate(int year, int month);

    CoursePerformanceDTO getCoursePerformance(int limit);

    RevenueByTypeDTO getRevenueByType(int year);

    ActiveUsersMetricsDTO getActiveUsersMetrics(LocalDate from, LocalDate to);
}
