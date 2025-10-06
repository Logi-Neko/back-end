package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.Subscription;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.repository.SubscriptionRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.dtos.responses.AdminStatDTO;
import exe2.learningapp.logineko.lesson.dtos.responses.CourseDTO;
import exe2.learningapp.logineko.lesson.dtos.responses.StatisticDTO;
import exe2.learningapp.logineko.lesson.entities.AccountQuestionResult;
import exe2.learningapp.logineko.lesson.entities.Course;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.Video;
import exe2.learningapp.logineko.lesson.repositories.AccountQuestionResultRepository;
import exe2.learningapp.logineko.lesson.repositories.VideoRepository;
import exe2.learningapp.logineko.lesson.services.CourseService;
import exe2.learningapp.logineko.lesson.services.LessonService;
import exe2.learningapp.logineko.lesson.services.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    AccountRepository accountRepository;
    AccountQuestionResultRepository accountQuestionResultRepository;
    LessonService lessonService;
    CourseService courseService;
    SubscriptionRepository subscriptionRepository;
    VideoRepository videoRepository;

    @Override
    @Transactional
    public StatisticDTO getStatistic(Long accountId, LocalDate from, LocalDate to) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        List<AccountQuestionResult> results = accountQuestionResultRepository.findByAccountAndCreatedAtBetween(
                account,
                from.atStartOfDay(),
                to.plusDays(1).atStartOfDay()
        );

        List<Video> videos = results.stream()
                .map(AccountQuestionResult::getVideo)
                .collect(Collectors.toMap(Video::getId, v -> v, (v1, v2) -> v1))
                .values().stream()
                .toList();

        List<Lesson> lessons = videos.stream()
                .map(Video::getLesson)
                .collect(Collectors.toMap(Lesson::getId, v -> v, (v1, v2) -> v1))
                .values().stream()
                .toList();

        List<CourseDTO> courses = lessons.stream()
                .map(Lesson::getCourse)
                .collect(Collectors.toMap(Course::getId, v -> v, (v1, v2) -> v1))
                .values().stream()
                .map(courseService::convertToCourseDTO)
                .toList();

        return StatisticDTO.builder()
                .courses(courses)
                .lessons(lessons.stream().map(lessonService::convertToLessonDTO).toList())
                .from(from)
                .to(to)
                .build();
    }

    @Override
    public AdminStatDTO getAdminStat(long year) {
        List<Account> allAccounts = accountRepository.findAll();
        List<Account> allPremiumAccounts = accountRepository.findAll()
                .stream()
                .filter(Account::getPremium)
                .toList();
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        Long totalUsers = (long) allAccounts.size();
        Long totalPremiumUsers = (long) allPremiumAccounts.size();
        Long totalRevenue = (long) allSubscriptions
                .stream()
                .mapToDouble(Subscription::getPrice)
                .sum();
        Long totalQuestions = videoRepository.count();
        Long totalRevenueInYear = (long) allSubscriptions
                .stream()
                .filter(s -> s.getCreatedAt().getYear() == year)
                .mapToDouble(Subscription::getPrice)
                .sum();
        Double averageRevenueInMonth = (double) totalRevenueInYear / 12;
        Long totalRevenueInYearBefore = (long) allSubscriptions
                .stream()
                .filter(s -> s.getCreatedAt().getYear() == (year - 1))
                .mapToDouble(Subscription::getPrice)
                .sum();
        Double yearOverYearGrowth = totalRevenueInYearBefore != 0 ? (double) (totalRevenueInYear - totalRevenueInYearBefore) / totalRevenueInYearBefore : 0;
        List<AdminStatDTO.MonthData> monthData = new ArrayList<>();
        LongStream.rangeClosed(1, 12).forEach(i -> {
            Predicate<Object> filterByMonthAndYear = s -> {
                LocalDateTime createdAt = null;
                if (s instanceof Account acc) createdAt = acc.getCreatedAt();
                else if (s instanceof Subscription sub) createdAt = sub.getCreatedAt();

                return createdAt != null && createdAt.getYear() == year && createdAt.getMonthValue() == i;
            };
            Long newUsers = allAccounts.stream()
                    .filter(filterByMonthAndYear)
                    .count();
            Long newPremiumUsers = allPremiumAccounts.stream()
                    .filter(filterByMonthAndYear)
                    .count();
            Long revenue = (long) allSubscriptions
                    .stream()
                    .filter(filterByMonthAndYear)
                    .mapToDouble(Subscription::getPrice)
                    .sum();
            Double monthOverMonthGrowth = 0.0;
            if (!monthData.isEmpty()) {
                long prevRevenue = monthData.getLast().getRevenue();
                monthOverMonthGrowth = (prevRevenue == 0)
                        ? 0
                        : ((double) (revenue - prevRevenue) / prevRevenue) * 100;
            }
            monthData.add(AdminStatDTO.MonthData.builder()
                    .month(i)
                    .newUsers(newUsers)
                    .newPremiumUsers(newPremiumUsers)
                    .revenue(revenue)
                    .monthOverMonthGrowth(monthOverMonthGrowth)
                    .build());
        });
        Long monthWithHighestRevenue = monthData.stream()
                .max(Comparator.
                        comparingLong(AdminStatDTO.MonthData::getRevenue)
                        .thenComparingLong(AdminStatDTO.MonthData::getNewUsers)
                )
                .map(AdminStatDTO.MonthData::getMonth)
                .orElse(-1L);
        return AdminStatDTO.builder()
                .totalUsers(totalUsers)
                .totalPremiumUsers(totalPremiumUsers)
                .totalRevenue(totalRevenue)
                .totalQuestions(totalQuestions)
                .year(year)
                .totalRevenueInYear(totalRevenueInYear)
                .averageRevenueInMonth(averageRevenueInMonth)
                .monthWithHighestRevenue(monthWithHighestRevenue)
                .yearOverYearGrowth(yearOverYearGrowth)
                .monthData(monthData)
                .build();
    }
}
