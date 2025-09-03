package exe2.learningapp.logineko.authentication.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exe2.learningapp.logineko.common.exception.ApiException;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = null;

        try {
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                JsonNode node = objectMapper.readTree(body);
                if (node.has("errorMessage")) {
                    errorMessage = node.get("errorMessage").asText();
                }
            }
        } catch (Exception ignored) {}

        ErrorCode errorCode;

        if (response.status() == 409) {
            if (errorMessage != null && errorMessage.contains("username")) {
                errorCode = ErrorCode.USER_EXISTED;
            } else if (errorMessage != null && errorMessage.contains("email")) {
                errorCode = ErrorCode.EMAIL_EXISTED;
            } else {
                errorCode = ErrorCode.USER_EXISTED; // fallback
            }
        } else {
            switch (response.status()) {
                case 400: errorCode = ErrorCode.ERR_BAD_REQUEST; break;
                case 401: errorCode = ErrorCode.ERR_UNAUTHORIZED; break;
                case 403: errorCode = ErrorCode.ERR_FORBIDDEN; break;
                case 404: errorCode = ErrorCode.ERR_NOT_FOUND; break;
                default: errorCode = ErrorCode.ERR_SERVER_ERROR; break;
            }
        }

        return new ApiException(errorCode);
    }


}
