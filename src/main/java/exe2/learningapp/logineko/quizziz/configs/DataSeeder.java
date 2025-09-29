package exe2.learningapp.logineko.quizziz.configs;


import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.enums.Role;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {
        if (accountRepository.count() == 0) {
            List<Account> accounts = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> Account.builder()
                            .email("user" + i + "@example.com")
                            .firstName("User")
                            .lastName(String.valueOf(i))
                            .username("user" + i)
                            .password("{noop}123456") // password để test, không encode
                            .userId("U" + i)
                            .active(true)
                            .roles(Set.of(Role.USER)) // nếu bạn có enum Role.USER
                            .build())
                    .toList();

            accountRepository.saveAll(accounts);
            System.out.println("✅ Seeded 10 test accounts!");
        }
    }
}
