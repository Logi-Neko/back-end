package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionStatusBreakdownDTO {
    Long totalSubscriptions;
    Long activeSubscriptions;
    Long inactiveSubscriptions;
    Long expiredSubscriptions;
    Double activePercentage;
    Double inactivePercentage;
    Double expiredPercentage;
}
