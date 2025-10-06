package exe2.learningapp.logineko.authentication.dtos.friend;

import exe2.learningapp.logineko.authentication.dtos.account.AccountDTO;
import exe2.learningapp.logineko.authentication.entity.FriendShip;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FriendDto(
        Long id,
        Long accountId,
        AccountDTO.AccountShowResponse friendAccount,
        FriendShip.StatusFriendShip status,
        LocalDateTime createdAt
) {
}
