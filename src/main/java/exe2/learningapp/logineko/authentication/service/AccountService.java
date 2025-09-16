package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.account.AccountDTO;
import exe2.learningapp.logineko.authentication.dtos.account.TokenExchangeResponse;

import java.util.List;

public interface AccountService {
    AccountDTO.AccountResponse register(AccountDTO.CreateAccountRequest request);
    List<AccountDTO.AccountResponse> getAllUsers();
    TokenExchangeResponse login(AccountDTO.LoginRequest loginRequest);
    void sendResetPasswordEmail(String username);
}
