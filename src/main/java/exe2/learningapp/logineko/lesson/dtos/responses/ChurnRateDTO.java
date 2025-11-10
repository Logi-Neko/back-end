package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChurnRateDTO {
    Integer year;
    Integer month;
    Long subscriptionsAtStart;
    Long newSubscriptions;
    Long canceledSubscriptions;
    Long expiredSubscriptions;
    Long subscriptionsAtEnd;
    Double churnRate;
    Double retentionRate;
    Double growthRate;
}
