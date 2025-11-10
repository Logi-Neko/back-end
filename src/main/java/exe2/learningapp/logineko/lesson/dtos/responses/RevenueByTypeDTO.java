package exe2.learningapp.logineko.lesson.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueByTypeDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TypeRevenue {
        String subscriptionType;
        Long count;
        Long revenue;
        Double percentage;
        Double averagePrice;
    }

    Integer year;
    Long totalRevenue;
    List<TypeRevenue> revenueByType;
    String mostProfitableType;
    String mostPopularType;
}
