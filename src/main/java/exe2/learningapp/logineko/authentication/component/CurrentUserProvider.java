package exe2.learningapp.logineko.authentication.component;

import exe2.learningapp.logineko.authentication.entity.Account;
import exe2.learningapp.logineko.authentication.repository.AccountRepository;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentUserProvider {
    private final AccountRepository accountRepository;

    public Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.ERR_UNAUTHORIZED); // Hoặc một lỗi phù hợp
        }

        String keycloakUserId = authentication.getName();

        // Gợi ý: Bạn nên đổi tên phương thức này thành findByKeycloakId cho rõ nghĩa
        return accountRepository.findByUserId(keycloakUserId)
                .orElseThrow(() -> new AppException(ErrorCode.ERR_NOT_FOUND));

    }
}
