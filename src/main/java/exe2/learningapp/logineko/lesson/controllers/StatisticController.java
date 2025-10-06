package exe2.learningapp.logineko.lesson.controllers;

import exe2.learningapp.logineko.common.dto.ApiResponse;
import exe2.learningapp.logineko.lesson.dtos.responses.AdminStatDTO;
import exe2.learningapp.logineko.lesson.dtos.responses.StatisticDTO;
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
}
