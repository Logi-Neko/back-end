package exe2.learningapp.logineko.authentication.dtos.subcription;

import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;

import java.time.LocalDate;

public record SubscriptionSummaryDto(
        Long id,
        String type,
        LocalDate startDate,
        LocalDate endDate,
        SubscriptionStatus subscriptionStatus,
        boolean isActive
) {}