package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionCreateDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionSummaryDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionUpdateDto;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.Subscription;
import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.repository.SubscriptionRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final AccountRepository accountRepository;

    @Override
    public SubscriptionDto createSubscription(SubscriptionCreateDto subscriptionCreateDto) {
        log.info("Creating subscription for account ID: {}", subscriptionCreateDto.accountId());

        // Kiểm tra tài khoản tồn tại
        Account account = accountRepository.findById(subscriptionCreateDto.accountId())
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        // Kiểm tra đã có subscription active chưa
        if (hasActiveSubscription(subscriptionCreateDto.accountId())) {
            throw new AppException(ErrorCode.ERR_EXISTS);
        }

        Subscription subscription = Subscription.builder()
                .account(account)
                .type(subscriptionCreateDto.type())
                .startDate(subscriptionCreateDto.startDate())
                .endDate(subscriptionCreateDto.endDate())
                .price(subscriptionCreateDto.price())
                .subscriptionStatus(subscriptionCreateDto.subscriptionStatus() != null
                        ? subscriptionCreateDto.subscriptionStatus() : SubscriptionStatus.ACTIVE)
                .build();

        subscription = subscriptionRepository.save(subscription);
        return mapToDto(subscription);
    }

    @Override
    public SubscriptionDto updateSubscription(Long id, SubscriptionUpdateDto subscriptionUpdateDto) {
        log.info("Updating subscription ID: {}", id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        if (subscriptionUpdateDto.type() != null) {
            subscription.setType(subscriptionUpdateDto.type());
        }
        if (subscriptionUpdateDto.startDate() != null) {
            subscription.setStartDate(subscriptionUpdateDto.startDate());
        }
        if (subscriptionUpdateDto.endDate() != null) {
            subscription.setEndDate(subscriptionUpdateDto.endDate());
        }
        if (subscriptionUpdateDto.price() != null) {
            subscription.setPrice(subscriptionUpdateDto.price());
        }
        if (subscriptionUpdateDto.subscriptionStatus() != null) {
            subscription.setSubscriptionStatus(subscriptionUpdateDto.subscriptionStatus());
        }

        subscription = subscriptionRepository.save(subscription);
        return mapToDto(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDto getSubscriptionById(Long id) {
        log.info("Getting subscription by ID: {}", id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        return mapToDto(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDto> getAllSubscriptions() {
        log.info("Getting all subscriptions");

        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptionsByAccountId(Long accountId) {
        log.info("Getting subscriptions for account ID: {}", accountId);

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        return subscriptionRepository.findByAccountId(accountId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDto getActiveSubscriptionByAccountId(Long accountId) {
        log.info("Getting active subscription for account ID: {}", accountId);

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        Subscription subscription = subscriptionRepository
                .findByAccountIdAndSubscriptionStatus(accountId, SubscriptionStatus.ACTIVE)
                .stream()
                .filter(s -> s.getEndDate().isAfter(LocalDate.now()))
                .findFirst()
                .orElse(null);

        return subscription != null ? mapToDto(subscription) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptionsByStatus(SubscriptionStatus status) {
        log.info("Getting subscriptions by status: {}", status);

        return subscriptionRepository.findBySubscriptionStatus(status)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionSummaryDto> getSubscriptionSummaryByAccountId(Long accountId) {
        log.info("Getting subscription summary for account ID: {}", accountId);

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        return subscriptionRepository.findByAccountId(accountId)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDto renewSubscription(Long id, LocalDate newEndDate) {
        log.info("Renewing subscription ID: {} with new end date: {}", id, newEndDate);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        if (newEndDate.isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        subscription.setEndDate(newEndDate);
        subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        subscription = subscriptionRepository.save(subscription);
        return mapToDto(subscription);
    }

    @Override
    public SubscriptionDto cancelSubscription(Long id) {
        log.info("Cancelling subscription ID: {}", id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        subscription.setSubscriptionStatus(SubscriptionStatus.INACTIVE);

        subscription = subscriptionRepository.save(subscription);
        return mapToDto(subscription);
    }

    @Override
    public SubscriptionDto activateSubscription(Long id) {
        log.info("Activating subscription ID: {}", id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        subscription = subscriptionRepository.save(subscription);
        return mapToDto(subscription);
    }




    @Override
    @Transactional(readOnly = true)
    public long calculateDaysRemaining(Long subscriptionId) {
        log.info("Calculating days remaining for subscription ID: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDate endDate = subscription.getEndDate();

        return today.isBefore(endDate) ? ChronoUnit.DAYS.between(today, endDate) : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public double getTotalRevenueByType(String type) {
        log.info("Calculating total revenue for type: {}", type);

        return subscriptionRepository.findByType(type)
                .stream()
                .mapToDouble(Subscription::getPrice)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public double getTotalRevenueBetween(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating total revenue between {} and {}", startDate, endDate);

        return subscriptionRepository.findByCreatedAtBetween(
                        startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                .stream()
                .mapToDouble(Subscription::getPrice)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public long countSubscriptionsByStatus(SubscriptionStatus status) {
        return subscriptionRepository.countBySubscriptionStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveSubscriptions() {
        return subscriptionRepository.countBySubscriptionStatusAndEndDateAfter(
                SubscriptionStatus.ACTIVE, LocalDate.now());
    }

    @Override
    public void deleteSubscription(Long id) {
        log.info("Deleting subscription ID: {}", id);

        if (!subscriptionRepository.existsById(id)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        subscriptionRepository.deleteById(id);
    }

    @Override
    public void deleteSubscriptionsByAccountId(Long accountId) {
        log.info("Deleting all subscriptions for account ID: {}", accountId);

        if (!accountRepository.existsById(accountId)) {
            throw new AppException(ErrorCode.ERR_NOT_FOUND);
        }

        subscriptionRepository.deleteByAccountId(accountId);
    }

    @Override
    public void updateExpiredSubscriptions() {
        log.info("Updating expired subscriptions");

        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findBySubscriptionStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now());

        expiredSubscriptions.forEach(subscription -> {
            subscription.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
        });

        subscriptionRepository.saveAll(expiredSubscriptions);
        log.info("Updated {} expired subscriptions", expiredSubscriptions.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getSubscriptionStatsByMonth(int year) {
        log.info("Getting subscription statistics for year: {}", year);

        return null;
    }

    private SubscriptionDto mapToDto(Subscription subscription) {
        long daysRemaining = subscription.getEndDate().isAfter(LocalDate.now())
                ? ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate()) : 0;

        boolean isActive = subscription.getSubscriptionStatus() == SubscriptionStatus.ACTIVE
                && subscription.getEndDate().isAfter(LocalDate.now());

        return new SubscriptionDto(
                subscription.getId(),
                subscription.getAccount().getId(),
                subscription.getAccount().getEmail(),
                subscription.getType(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getPrice(),
                subscription.getSubscriptionStatus(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt(),
                isActive,
                daysRemaining
        );
    }

    private SubscriptionSummaryDto mapToSummaryDto(Subscription subscription) {
        boolean isActive = subscription.getSubscriptionStatus() == SubscriptionStatus.ACTIVE
                && subscription.getEndDate().isAfter(LocalDate.now());

        return new SubscriptionSummaryDto(
                subscription.getId(),
                subscription.getType(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getSubscriptionStatus(),
                isActive
        );
    }

    private boolean hasActiveSubscription(Long accountId) {
        return subscriptionRepository.existsByAccountIdAndSubscriptionStatusAndEndDateAfter(
                accountId, SubscriptionStatus.ACTIVE, LocalDate.now());
    }
}