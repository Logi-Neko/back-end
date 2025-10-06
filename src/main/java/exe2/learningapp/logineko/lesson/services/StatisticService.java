package exe2.learningapp.logineko.lesson.services;

import exe2.learningapp.logineko.lesson.dtos.responses.AdminStatDTO;
import exe2.learningapp.logineko.lesson.dtos.responses.StatisticDTO;

import java.time.LocalDate;

public interface StatisticService {
    StatisticDTO getStatistic(Long accountId, LocalDate from, LocalDate to);

    AdminStatDTO getAdminStat(long year);
}
