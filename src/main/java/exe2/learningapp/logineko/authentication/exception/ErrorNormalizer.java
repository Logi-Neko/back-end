package exe2.learningapp.logineko.authentication.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exe2.learningapp.logineko.common.exception.AppException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ErrorNormalizer {
    private final ObjectMapper objectMapper;
    private final Map<String, ErrorCode> errorCodeMap = Map.of(
            "User exists with same username", ErrorCode.USER_EXISTED,
            "User exists with same email", ErrorCode.EMAIL_EXISTED,
            "User name is missing", ErrorCode.USERNAME_IS_MISSING
    );
    public AppException handleKeycloakError(FeignException e) {
        try {
            log.warn("API Exception: {} - {}", e.status(), e.getMessage());
            var response= objectMapper.readValue(e.contentUTF8(),KeycloakError.class);
            if(Objects.nonNull(response.getErrorMessage()) && Objects.nonNull(errorCodeMap.get(response.getErrorMessage()))) {
                return new AppException(errorCodeMap.get(response.getErrorMessage()));
            }
        } catch (JsonProcessingException ex) {
           log.error("không thể đọc lỗi",ex);
        }
        return new AppException(ErrorCode.ERR_SERVER_ERROR);
    }
}
