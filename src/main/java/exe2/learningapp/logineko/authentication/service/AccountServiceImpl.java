package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.client.IdentityClient;
import exe2.learningapp.logineko.authentication.dtos.AccountDTO;
import exe2.learningapp.logineko.authentication.dtos.TokenExchangeParams;
import exe2.learningapp.logineko.authentication.dtos.UserInfo;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.Role;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService , UserDetailsService {
    private final AccountRepository accountRepository;
    private final IdentityClient identityClient;

    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public AccountDTO.AccountResponse register(AccountDTO.CreateAccountRequest request) {
        //get account from keycloak
        var token = identityClient.exchangeToken(TokenExchangeParams.builder()
                .grantType("client_credentials")
                .clientId(clientId)
                .clientSecret(clientSecret).scope("openid")
                .build());

        //exchange client Token

        //create user in keycloak

        //get user info from keycloak

        var account = Account.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .password(request.password()) // In a real application, ensure to hash the password
                .roles(Collections.singleton(Role.USER)) // Default role
                .active(true)
                .build();
        accountRepository.save(account);
        return mapToDTO(account);
    }

    @Override
    public List<AccountDTO.AccountResponse> getAllUsers() {
        return List.of();
    }

    private AccountDTO.AccountResponse mapToDTO(Account account) {
        return new AccountDTO.AccountResponse(
                account.getId(),
                account.getEmail(),
                account.getFirstName(),
                account.getLastName(),
                account.getUsername()
        );
    }
}
