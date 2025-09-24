package exe2.learningapp.logineko.authentication.scheduler;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class updatePremiumStatus {
    private final AccountRepository accountRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updatePremiumStatuses() {
        List<Account> accounts = accountRepository.findAll();
        for (Account acc : accounts) {
            if (acc.getPremiumUntil() != null && acc.getPremiumUntil().isBefore(LocalDate.now())) {
                acc.setPremium(false);
                accountRepository.save(acc);
            }
        }
    }
}
