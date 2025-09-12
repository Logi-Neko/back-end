package exe2.learningapp.logineko.authentication.dtos.subcription;

import exe2.learningapp.logineko.authentication.entity.enums.CharacterRarity;
import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionDto(
        Long id,
        Long accountId,
        String accountEmail,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        double price,
        SubscriptionStatus subscriptionStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isActive,
        long daysRemaining
) {}