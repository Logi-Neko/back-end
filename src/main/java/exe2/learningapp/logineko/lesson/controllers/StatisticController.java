package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.responses.*;
import exe2.learningapp.logineko.lesson.services.StatisticService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Statistics", description = "API thống kê tiến độ học")
@RequiredArgsConstructor
public class StatisticController {
    StatisticService statisticService;

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<StatisticDTO>> findStatistic(
            @PathVariable Long accountId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
            ) {
        return ResponseEntity.ok(
                ApiResponse.success(statisticService.getStatistic(accountId, from, to))
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<AdminStatDTO>> findStatisticAdmin(@RequestParam(defaultValue = "2025") Long year) {
        return ResponseEntity.ok(
                ApiResponse.success(statisticService.getAdminStat(year))
        );
    }

    @GetMapping("/admin/subscriptions/status")
    public ResponseEntity<ApiResponse<SubscriptionStatusBreakdownDTO>> getSubscriptionStatusBreakdown() {
        return ResponseEntity.ok(
                ApiResponse.success(statisticService.getSubscriptionStatusBreakdown())
        );
    }

    @GetMapping("/admin/churn")
    public ResponseEntity<ApiResponse<ChurnRateDTO>> getChurnRate(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(statisticService.getChurnRate(year, month))
        );
    }

    @GetMapping("/admin/courses/popular")
    public ResponseEntity<ApiResponse<CoursePerformanceDTO>> getCoursePerformance(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(statisticService.getCoursePerformance(limit))
        );
    }

    @GetMapping("/admin/revenue/by-type")
    public ResponseEntity<ApiResponse<RevenueByTypeDTO>> getRevenueByType(
            @RequestParam(defaultValue = "2025") int year
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(statisticService.getRevenueByType(year))
        );
    }

    @GetMapping("/admin/users/activity")
    public ResponseEntity<ApiResponse<ActiveUsersMetricsDTO>> getActiveUsersMetrics(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(statisticService.getActiveUsersMetrics(from, to))
        );
    }
}
