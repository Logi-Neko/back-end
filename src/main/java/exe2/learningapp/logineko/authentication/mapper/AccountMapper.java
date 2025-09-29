package exe2.learningapp.logineko.authentication.mapper;

import exe2.learningapp.logineko.authentication.dtos.account.AccountDTO;
import exe2.learningapp.logineko.authentication.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    public AccountDTO.AccountShowResponse toAccountShowResponseDTO(Account account) {
        return AccountDTO.AccountShowResponse.builder()
                .id(account.getId())
                .fullName(account.getLastName())
                .avatarUrl(account.getAvatarUrl())
                .totalStar(account.getTotalStar())
                .premium(account.getPremium())
                .build();
    }
}
