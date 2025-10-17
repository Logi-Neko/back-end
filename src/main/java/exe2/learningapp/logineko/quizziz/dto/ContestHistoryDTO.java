package exe2.learningapp.logineko.quizziz.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Builder
@Getter
public class ContestHistoryDTO {
    private Long contestId;
    private String contestTitle;
    private LocalDateTime startTime;
    private int score;
    private Integer rank;
}
