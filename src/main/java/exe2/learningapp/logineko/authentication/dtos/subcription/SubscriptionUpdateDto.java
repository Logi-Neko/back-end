package exe2.learningapp.logineko.authentication.dtos.subcription;

import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record SubscriptionUpdateDto(
        String type,
        LocalDate startDate,
        LocalDate endDate,
        @Positive(message = "Giá phải là số dương")
        Double price,
        SubscriptionStatus subscriptionStatus
) {}