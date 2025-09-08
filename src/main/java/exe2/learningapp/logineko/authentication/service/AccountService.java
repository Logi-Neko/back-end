package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.AccountDTO;
import exe2.learningapp.logineko.authentication.dtos.TokenExchangeResponse;
import exe2.learningapp.logineko.authentication.dtos.UserInfo;
import exe2.learningapp.logineko.authentication.entity.Account;

import java.util.List;

public interface AccountService {
    AccountDTO.AccountResponse register(AccountDTO.CreateAccountRequest request);
    List<AccountDTO.AccountResponse> getAllUsers();
    TokenExchangeResponse exchangeToken();
}
