package exe2.learningapp.logineko.lesson.services.impls;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.Subscription;
import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.repository.SubscriptionRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import exe2.learningapp.logineko.lesson.dtos.responses.*;
import exe2.learningapp.logineko.lesson.entities.AccountQuestionResult;
import exe2.learningapp.logineko.lesson.entities.Course;
import exe2.learningapp.logineko.lesson.entities.Lesson;
import exe2.learningapp.logineko.lesson.entities.Video;
import exe2.learningapp.logineko.lesson.repositories.AccountQuestionResultRepository;
import exe2.learningapp.logineko.lesson.repositories.CourseRepository;
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
import java.time.YearMonth;
import java.util.*;
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
    CourseRepository courseRepository;

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
        List<Subscription> allSubscriptions = subscriptionRepository.findBySubscriptionStatus(SubscriptionStatus.ACTIVE);

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
                if (LocalDate.now().getYear() == year && i < LocalDate.now().getMonthValue()) {
                    long prevRevenue = monthData.getLast().getRevenue();
                    monthOverMonthGrowth = (prevRevenue == 0)
                            ? 0
                            : ((double) (revenue - prevRevenue) / prevRevenue) * 100;
                }
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

    @Override
    public SubscriptionStatusBreakdownDTO getSubscriptionStatusBreakdown() {
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        long totalSubscriptions = allSubscriptions.size();
        long activeSubscriptions = allSubscriptions.stream()
                .filter(s -> s.getSubscriptionStatus() == SubscriptionStatus.ACTIVE)
                .count();
        long inactiveSubscriptions = allSubscriptions.stream()
                .filter(s -> s.getSubscriptionStatus() == SubscriptionStatus.INACTIVE)
                .count();
        long expiredSubscriptions = allSubscriptions.stream()
                .filter(s -> s.getSubscriptionStatus() == SubscriptionStatus.EXPIRED)
                .count();

        double activePercentage = totalSubscriptions > 0 ? (double) activeSubscriptions / totalSubscriptions * 100 : 0;
        double inactivePercentage = totalSubscriptions > 0 ? (double) inactiveSubscriptions / totalSubscriptions * 100 : 0;
        double expiredPercentage = totalSubscriptions > 0 ? (double) expiredSubscriptions / totalSubscriptions * 100 : 0;

        return SubscriptionStatusBreakdownDTO.builder()
                .totalSubscriptions(totalSubscriptions)
                .activeSubscriptions(activeSubscriptions)
                .inactiveSubscriptions(inactiveSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .activePercentage(activePercentage)
                .inactivePercentage(inactivePercentage)
                .expiredPercentage(expiredPercentage)
                .build();
    }

    @Override
    public ChurnRateDTO getChurnRate(int year, int month) {
        YearMonth currentMonth = YearMonth.of(year, month);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        LocalDateTime prevMonthEnd = previousMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        // Subscriptions active at the start of the month (end of previous month)
        long subscriptionsAtStart = allSubscriptions.stream()
                .filter(s -> s.getCreatedAt().isBefore(monthStart) || s.getCreatedAt().isEqual(monthStart))
                .filter(s -> s.getSubscriptionStatus() == SubscriptionStatus.ACTIVE
                        || (s.getCreatedAt().isBefore(monthStart) && s.getUpdatedAt().isAfter(prevMonthEnd)))
                .count();

        // New subscriptions created in this month
        long newSubscriptions = allSubscriptions.stream()
                .filter(s -> s.getCreatedAt().isAfter(monthStart.minusSeconds(1)) && s.getCreatedAt().isBefore(monthEnd.plusSeconds(1)))
                .filter(s -> s.getSubscriptionStatus() == SubscriptionStatus.ACTIVE)
                .count();

        // Canceled subscriptions (became inactive) during this month
        long canceledSubscriptions = allSubscriptions.stream()
                .filter(s -> s.getSubscriptionStatus() == SubscriptionStatus.INACTIVE)
                .filter(s -> s.getUpdatedAt().isAfter(monthStart.minusSeconds(1)) && s.getUpdatedAt().isBefore(monthEnd.plusSeconds(1)))
                .count();

        // Expired subscriptions during this month
        long expiredSubscriptions = allSubscriptions.stream()
                .filter(s -> s.getSubscriptionStatus() == SubscriptionStatus.EXPIRED)
                .filter(s -> s.getEndDate() != null
                        && s.getEndDate().isAfter(currentMonth.atDay(1).minusDays(1))
                        && s.getEndDate().isBefore(currentMonth.atEndOfMonth().plusDays(1)))
                .count();

        long subscriptionsAtEnd = subscriptionsAtStart + newSubscriptions - canceledSubscriptions - expiredSubscriptions;

        // Churn rate = (canceled + expired) / subscriptions at start
        double churnRate = subscriptionsAtStart > 0
                ? ((double) (canceledSubscriptions + expiredSubscriptions) / subscriptionsAtStart) * 100
                : 0;

        // Retention rate = 100 - churn rate
        double retentionRate = 100 - churnRate;

        // Growth rate = (subscriptions at end - subscriptions at start) / subscriptions at start
        double growthRate = subscriptionsAtStart > 0
                ? ((double) (subscriptionsAtEnd - subscriptionsAtStart) / subscriptionsAtStart) * 100
                : 0;

        return ChurnRateDTO.builder()
                .year(year)
                .month(month)
                .subscriptionsAtStart(subscriptionsAtStart)
                .newSubscriptions(newSubscriptions)
                .canceledSubscriptions(canceledSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .subscriptionsAtEnd(subscriptionsAtEnd)
                .churnRate(churnRate)
                .retentionRate(retentionRate)
                .growthRate(growthRate)
                .build();
    }

    @Override
    public CoursePerformanceDTO getCoursePerformance(int limit) {
        List<Course> allCourses = courseRepository.findAll();
        List<AccountQuestionResult> allResults = accountQuestionResultRepository.findAll();

        // Group results by course
        Map<Long, List<AccountQuestionResult>> resultsByCourse = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getVideo().getLesson().getCourse().getId()));

        List<CoursePerformanceDTO.CourseStats> courseStatsList = new ArrayList<>();

        for (Course course : allCourses) {
            List<AccountQuestionResult> courseResults = resultsByCourse.getOrDefault(course.getId(), new ArrayList<>());

            long totalLessons = course.getTotalLesson();
            long totalVideos = videoRepository.findAll().stream()
                    .filter(v -> v.getLesson().getCourse().getId().equals(course.getId()))
                    .count();
            long totalQuestions = courseResults.size();

            long uniqueStudents = courseResults.stream()
                    .map(r -> r.getAccount().getId())
                    .distinct()
                    .count();

            long totalAttempts = courseResults.size();

            // Calculate average score as percentage of correct answers
            double averageScore = courseResults.isEmpty() ? 0 :
                    (double) courseResults.stream()
                            .filter(AccountQuestionResult::getIsCorrect)
                            .count() / courseResults.size() * 100;

            // Completion rate calculation (simplified - based on students who attempted questions)
            double completionRate = totalVideos > 0 && uniqueStudents > 0
                    ? ((double) totalAttempts / (totalVideos * uniqueStudents)) * 100
                    : 0;

            CoursePerformanceDTO.CourseStats stats = CoursePerformanceDTO.CourseStats.builder()
                    .courseId(course.getId())
                    .courseName(course.getName())
                    .description(course.getDescription())
                    .thumbnailUrl(course.getThumbnailUrl())
                    .isPremium(course.getIsPremium())
                    .totalLessons(totalLessons)
                    .totalVideos(totalVideos)
                    .totalQuestions(totalQuestions)
                    .uniqueStudents(uniqueStudents)
                    .totalAttempts(totalAttempts)
                    .averageScore(averageScore)
                    .completionRate(Math.min(completionRate, 100.0))
                    .price(course.getPrice())
                    .build();

            courseStatsList.add(stats);
        }

        // Sort by unique students (popularity) and limit
        List<CoursePerformanceDTO.CourseStats> popularCourses = courseStatsList.stream()
                .sorted(Comparator.comparingLong(CoursePerformanceDTO.CourseStats::getUniqueStudents).reversed())
                .limit(limit)
                .toList();

        long totalCourses = allCourses.size();
        long totalPremiumCourses = allCourses.stream().filter(Course::getIsPremium).count();
        long totalFreeCourses = totalCourses - totalPremiumCourses;
        long activeCourses = allCourses.stream().filter(Course::getIsActive).count();
        double averageStudentsPerCourse = totalCourses > 0
                ? courseStatsList.stream().mapToLong(CoursePerformanceDTO.CourseStats::getUniqueStudents).average().orElse(0)
                : 0;

        return CoursePerformanceDTO.builder()
                .popularCourses(popularCourses)
                .totalCourses(totalCourses)
                .totalPremiumCourses(totalPremiumCourses)
                .totalFreeCourses(totalFreeCourses)
                .activeCourses(activeCourses)
                .averageStudentsPerCourse(averageStudentsPerCourse)
                .build();
    }

    @Override
    public RevenueByTypeDTO getRevenueByType(int year) {
        List<Subscription> subscriptionsInYear = subscriptionRepository.findBySubscriptionStatus(SubscriptionStatus.ACTIVE)
                .stream()
                .filter(s -> s.getCreatedAt().getYear() == year)
                .toList();

        long totalRevenue = (long) subscriptionsInYear.stream()
                .mapToDouble(Subscription::getPrice)
                .sum();

        // Group by subscription type
        Map<String, List<Subscription>> byType = subscriptionsInYear.stream()
                .collect(Collectors.groupingBy(Subscription::getType));

        List<RevenueByTypeDTO.TypeRevenue> revenueByTypeList = new ArrayList<>();

        for (Map.Entry<String, List<Subscription>> entry : byType.entrySet()) {
            String type = entry.getKey();
            List<Subscription> subs = entry.getValue();

            long count = subs.size();
            long revenue = (long) subs.stream().mapToDouble(Subscription::getPrice).sum();
            double percentage = totalRevenue > 0 ? (double) revenue / totalRevenue * 100 : 0;
            double averagePrice = count > 0 ? (double) revenue / count : 0;

            revenueByTypeList.add(RevenueByTypeDTO.TypeRevenue.builder()
                    .subscriptionType(type)
                    .count(count)
                    .revenue(revenue)
                    .percentage(percentage)
                    .averagePrice(averagePrice)
                    .build());
        }

        // Sort by revenue descending
        revenueByTypeList.sort(Comparator.comparingLong(RevenueByTypeDTO.TypeRevenue::getRevenue).reversed());

        String mostProfitableType = revenueByTypeList.isEmpty() ? null : revenueByTypeList.get(0).getSubscriptionType();
        String mostPopularType = byType.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse(null);

        return RevenueByTypeDTO.builder()
                .year(year)
                .totalRevenue(totalRevenue)
                .revenueByType(revenueByTypeList)
                .mostProfitableType(mostProfitableType)
                .mostPopularType(mostPopularType)
                .build();
    }

    @Override
    public ActiveUsersMetricsDTO getActiveUsersMetrics(LocalDate from, LocalDate to) {
        List<AccountQuestionResult> results = accountQuestionResultRepository.findAll().stream()
                .filter(r -> {
                    LocalDate resultDate = r.getCreatedAt().toLocalDate();
                    return !resultDate.isBefore(from) && !resultDate.isAfter(to);
                })
                .toList();

        // Get unique active users
        Set<Long> activeUserIds = results.stream()
                .map(r -> r.getAccount().getId())
                .collect(Collectors.toSet());

        long totalActiveUsers = activeUserIds.size();

        // Get accounts and check premium status
        List<Account> activeAccounts = accountRepository.findAllById(activeUserIds);
        long totalPremiumActiveUsers = activeAccounts.stream()
                .filter(Account::getPremium)
                .count();
        long totalFreeActiveUsers = totalActiveUsers - totalPremiumActiveUsers;

        // Calculate DAU, WAU, MAU based on the most recent data
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);
        LocalDate lastWeek = now.minusWeeks(1);
        LocalDate lastMonth = now.minusMonths(1);

        long dailyActiveUsers = results.stream()
                .filter(r -> r.getCreatedAt().toLocalDate().equals(yesterday))
                .map(r -> r.getAccount().getId())
                .distinct()
                .count();

        long weeklyActiveUsers = results.stream()
                .filter(r -> !r.getCreatedAt().toLocalDate().isBefore(lastWeek))
                .map(r -> r.getAccount().getId())
                .distinct()
                .count();

        long monthlyActiveUsers = results.stream()
                .filter(r -> !r.getCreatedAt().toLocalDate().isBefore(lastMonth))
                .map(r -> r.getAccount().getId())
                .distinct()
                .count();

        double averageAttemptsPerUser = totalActiveUsers > 0
                ? (double) results.size() / totalActiveUsers
                : 0;

        // Group by date for daily activities
        Map<LocalDate, List<AccountQuestionResult>> resultsByDate = results.stream()
                .collect(Collectors.groupingBy(r -> r.getCreatedAt().toLocalDate()));

        List<ActiveUsersMetricsDTO.DailyActivity> dailyActivities = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            List<AccountQuestionResult> dayResults = resultsByDate.getOrDefault(date, new ArrayList<>());

            long activeUsersOnDay = dayResults.stream()
                    .map(r -> r.getAccount().getId())
                    .distinct()
                    .count();

            long attemptsOnDay = dayResults.size();
            long questionsOnDay = dayResults.stream()
                    .map(r -> r.getVideo().getId())
                    .distinct()
                    .count();

            dailyActivities.add(ActiveUsersMetricsDTO.DailyActivity.builder()
                    .date(date)
                    .activeUsers(activeUsersOnDay)
                    .totalAttempts(attemptsOnDay)
                    .totalQuestions(questionsOnDay)
                    .build());
        }

        return ActiveUsersMetricsDTO.builder()
                .from(from)
                .to(to)
                .totalActiveUsers(totalActiveUsers)
                .totalPremiumActiveUsers(totalPremiumActiveUsers)
                .totalFreeActiveUsers(totalFreeActiveUsers)
                .dailyActiveUsers(dailyActiveUsers)
                .weeklyActiveUsers(weeklyActiveUsers)
                .monthlyActiveUsers(monthlyActiveUsers)
                .averageAttemptsPerUser(averageAttemptsPerUser)
                .dailyActivities(dailyActivities)
                .build();
    }
}
