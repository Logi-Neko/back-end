package exe2.learningapp.logineko.authentication.dtos.subcription;

import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;

public record SubscriptionCreateDto(
        @NotNull(message = "ID tài khoản là bắt buộc")
        Long accountId,

        @NotBlank(message = "Loại gói đăng ký là bắt buộc")
        String type,

        @NotNull(message = "Ngày bắt đầu là bắt buộc")
        LocalDate startDate,

        @NotNull(message = "Ngày kết thúc là bắt buộc")
        @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
        LocalDate endDate,

        @Positive(message = "Giá tiền phải là số dương")
        double price,

        SubscriptionStatus subscriptionStatus
) {}