package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.client.IdentityClient;
import exe2.learningapp.logineko.authentication.dtos.*;
import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.entity.Role;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.authentication.exception.ErrorNormalizer;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService , UserDetailsService {
    private final AccountRepository accountRepository;
    private final IdentityClient identityClient;
    private final ErrorNormalizer errorNormalizer;

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
        try {
            // Lấy token
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "client_credentials");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("scope", "openid");
            TokenExchangeResponse response = identityClient.exchangeToken(params);

            // Tạo user trên Keycloak
            var creationUser = identityClient.createUser(
                    "Bearer " + response.accessToken(),
                    UserCreationParams.builder()
                            .username(request.username())
                            .email(request.email())
                            .firstName(request.firstName())
                            .lastName(request.lastName())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credentials.builder()
                                    .type("password")
                                    .value(request.password())
                                    .temporary(false)
                                    .build()))
                            .build()
            );

            String userId = extractUserIdFromLocation(creationUser);
            log.info("Created user in Keycloak with ID: {}", userId);

            // Lưu account trong DB
            var account = Account.builder()
                    .email(request.email())
                    .firstName(request.firstName())
                    .userId(userId)
                    .lastName(request.lastName())
                    .username(request.username())
                    .password(request.password()) // TODO: hash password
                    .roles(Collections.singleton(Role.USER))
                    .active(true)
                    .build();

            accountRepository.saveAndFlush(account);
            return mapToDTO(account);
        }
        catch (FeignException e) {
            throw errorNormalizer.handleKeycloakError(e);
        }

    }


    private String extractUserIdFromLocation(ResponseEntity<?> response) {
        String location = response.getHeaders().getFirst("Location");
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location header is null or empty");
        }
        String[] segments = location.split("/");
        return segments[segments.length - 1];
    }

    @Override
    public List<AccountDTO.AccountResponse> getAllUsers() {
        List<Account> accounts = accountRepository.findAll();
        if(accounts.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return accounts.stream().map(this::mapToDTO).toList();
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
