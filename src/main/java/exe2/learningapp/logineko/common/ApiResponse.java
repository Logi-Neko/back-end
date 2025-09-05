package exe2.learningapp.logineko.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import exe2.learningapp.logineko.common.exception.ErrorCode;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    int status;
    String code;        // Added to match ErrorCode structure
    String message;
    T data;
    String path;
    List<String> errors;
    Map<String, Object> metadata;

    // ===========================
    // SUCCESS RESPONSES WITH ERROR CODES
    // ===========================

    public static <T> ApiResponse<T> success(T data, ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .data(data)
                .message(errorCode.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, ErrorCode.GEN_ACTION_SUCCESS);
    }

    public static ApiResponse<Void> success(ErrorCode errorCode) {
        return success(null, errorCode);
    }

    public static ApiResponse<Void> success() {
        return success(ErrorCode.GEN_ACTION_SUCCESS);
    }

    // Created responses
    public static <T> ApiResponse<T> created(T data, ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .data(data)
                .message(errorCode.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return created(data, ErrorCode.GEN_CREATED_SUCCESS);
    }

    public static ApiResponse<Void> created() {
        return created(null, ErrorCode.GEN_CREATED_SUCCESS);
    }

    // Updated responses
    public static <T> ApiResponse<T> updated(T data) {
        return success(data, ErrorCode.GEN_UPDATED_SUCCESS);
    }

    public static ApiResponse<Void> updated() {
        return success(ErrorCode.GEN_UPDATED_SUCCESS);
    }

    // Deleted responses
    public static <T> ApiResponse<T> deleted(T data) {
        return success(data, ErrorCode.GEN_DELETED_SUCCESS);
    }

    public static ApiResponse<Void> deleted() {
        return success(ErrorCode.GEN_DELETED_SUCCESS);
    }

    // Retrieved responses
    public static <T> ApiResponse<T> retrieved(T data) {
        return success(data, ErrorCode.GEN_RETRIEVED_SUCCESS);
    }

    // No Content response
    public static ApiResponse<Void> noContent() {
        return success(ErrorCode.GEN_NO_CONTENT);
    }

    // ===========================
    // ERROR RESPONSES WITH ERROR CODES
    // ===========================

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return ApiResponse.<Void>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static ApiResponse<Void> error(ErrorCode errorCode, List<String> errors) {
        return ApiResponse.<Void>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();
    }

    // Error with custom message (for parameterized messages)
    public static ApiResponse<Void> error(ErrorCode errorCode, String customMessage) {
        return ApiResponse.<Void>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(customMessage)
                .build();
    }

    // ===========================
    // AUTHENTICATION RESPONSES
    // ===========================

    public static ApiResponse<Void> authSuccess() {
        return success(ErrorCode.AUTH_SUCCESS);
    }

    public static <T> ApiResponse<T> authSuccess(T data) {
        return success(data, ErrorCode.AUTH_SUCCESS);
    }

    public static ApiResponse<Void> authFailure() {
        return error(ErrorCode.AUTH_FAILURE);
    }

    public static ApiResponse<Void> authInvalidCredentials() {
        return error(ErrorCode.AUTH_INVALID_CREDENTIALS);
    }

    public static ApiResponse<Void> authAccountLocked() {
        return error(ErrorCode.AUTH_ACCOUNT_LOCKED);
    }

    public static ApiResponse<Void> authSessionExpired() {
        return error(ErrorCode.AUTH_SESSION_EXPIRED);
    }

    public static ApiResponse<Void> authOtpRequired() {
        return error(ErrorCode.AUTH_OTP_REQUIRED);
    }

    public static ApiResponse<Void> authOtpInvalid() {
        return error(ErrorCode.AUTH_OTP_INVALID);
    }

    public static ApiResponse<Void> authLogoutSuccess() {
        return success(ErrorCode.AUTH_LOGOUT_SUCCESS);
    }

    // ===========================
    // REGISTRATION RESPONSES
    // ===========================

    public static <T> ApiResponse<T> regSuccess(T data) {
        return success(data, ErrorCode.REG_SUCCESS);
    }

    public static ApiResponse<Void> regSuccess() {
        return success(ErrorCode.REG_SUCCESS);
    }

    public static ApiResponse<Void> regFailure() {
        return error(ErrorCode.REG_FAILURE);
    }

    public static ApiResponse<Void> regEmailRequired() {
        return error(ErrorCode.REG_EMAIL_REQUIRED);
    }

    public static ApiResponse<Void> regEmailInvalid() {
        return error(ErrorCode.REG_EMAIL_INVALID);
    }

    public static ApiResponse<Void> regPasswordMismatch() {
        return error(ErrorCode.REG_PASSWORD_MISMATCH);
    }

    public static ApiResponse<Void> regUsernameTaken() {
        return error(ErrorCode.REG_USERNAME_TAKEN);
    }

    public static ApiResponse<Void> regEmailTaken() {
        return error(ErrorCode.REG_EMAIL_TAKEN);
    }

    // ===========================
    // COMMON ERROR RESPONSES
    // ===========================

    public static ApiResponse<Void> badRequest() {
        return error(ErrorCode.ERR_BAD_REQUEST);
    }

    public static ApiResponse<Void> badRequest(List<String> errors) {
        return error(ErrorCode.ERR_BAD_REQUEST, errors);
    }

    public static ApiResponse<Void> unauthorized() {
        return error(ErrorCode.ERR_UNAUTHORIZED);
    }

    public static ApiResponse<Void> forbidden() {
        return error(ErrorCode.ERR_FORBIDDEN);
    }

    public static ApiResponse<Void> notFound() {
        return error(ErrorCode.ERR_NOT_FOUND);
    }

    public static ApiResponse<Void> serverError() {
        return error(ErrorCode.ERR_SERVER_ERROR);
    }

    public static ApiResponse<Void> timeout() {
        return error(ErrorCode.ERR_TIMEOUT);
    }

    // ===========================
    // PERMISSION RESPONSES
    // ===========================

    public static ApiResponse<Void> permissionDenied() {
        return error(ErrorCode.PERM_DENIED);
    }

    public static ApiResponse<Void> permissionGranted() {
        return success(ErrorCode.PERM_GRANTED);
    }

    public static ApiResponse<Void> authRequired() {
        return error(ErrorCode.PERM_AUTH_REQUIRED);
    }

    public static ApiResponse<Void> roleRequired() {
        return error(ErrorCode.PERM_ROLE_REQUIRED);
    }

    public static ApiResponse<Void> accessRestricted() {
        return error(ErrorCode.PERM_ACCESS_RESTRICTED);
    }

    // ===========================
    // VALIDATION RESPONSES
    // ===========================

    public static ApiResponse<Void> validationError(String fieldName) {
        String message = ErrorCode.VAL_FIELD_REQUIRED.getMessage().replace("{0}", fieldName);
        return error(ErrorCode.VAL_FIELD_REQUIRED, message);
    }

    public static ApiResponse<Void> validationError(String fieldName, String reason) {
        String message = ErrorCode.VAL_FIELD_INVALID.getMessage().replace("{0}", fieldName);
        return error(ErrorCode.VAL_FIELD_INVALID, message);
    }

    public static ApiResponse<Void> validationMinLength(String fieldName, int minLength) {
        String message = ErrorCode.VAL_FIELD_MIN_LENGTH.getMessage()
                .replace("{0}", fieldName)
                .replace("{1}", String.valueOf(minLength));
        return error(ErrorCode.VAL_FIELD_MIN_LENGTH, message);
    }

    public static ApiResponse<Void> validationMaxLength(String fieldName, int maxLength) {
        String message = ErrorCode.VAL_FIELD_MAX_LENGTH.getMessage()
                .replace("{0}", fieldName)
                .replace("{1}", String.valueOf(maxLength));
        return error(ErrorCode.VAL_FIELD_MAX_LENGTH, message);
    }

    public static ApiResponse<Void> validationEmailInvalid(String fieldName) {
        String message = ErrorCode.VAL_FIELD_EMAIL_INVALID.getMessage().replace("{0}", fieldName);
        return error(ErrorCode.VAL_FIELD_EMAIL_INVALID, message);
    }

    // ===========================
    // UPLOAD RESPONSES
    // ===========================

    public static <T> ApiResponse<T> uploadSuccess(T data) {
        return success(data, ErrorCode.UPLOAD_SUCCESS);
    }

    public static ApiResponse<Void> uploadSuccess() {
        return success(ErrorCode.UPLOAD_SUCCESS);
    }

    public static ApiResponse<Void> uploadFailure() {
        return error(ErrorCode.UPLOAD_FAILURE);
    }

    public static ApiResponse<Void> uploadTooLarge() {
        return error(ErrorCode.UPLOAD_TOO_LARGE);
    }

    public static ApiResponse<Void> uploadTypeNotSupported() {
        return error(ErrorCode.UPLOAD_TYPE_NOT_SUPPORTED);
    }

    public static ApiResponse<Void> uploadNotFound() {
        return error(ErrorCode.UPLOAD_NOT_FOUND);
    }

    // ===========================
    // SYSTEM RESPONSES
    // ===========================

    public static ApiResponse<Void> systemBusy() {
        return error(ErrorCode.SYS_BUSY);
    }

    public static ApiResponse<Void> systemMaintenance() {
        return error(ErrorCode.SYS_MAINTENANCE);
    }

    public static ApiResponse<Void> systemUnavailable() {
        return error(ErrorCode.SYS_UNAVAILABLE);
    }

    // ===========================
    // NOTIFICATION RESPONSES
    // ===========================

    public static ApiResponse<Void> notificationSent() {
        return success(ErrorCode.NOTIF_SENT);
    }

    public static ApiResponse<Void> notificationFailed() {
        return error(ErrorCode.NOTIF_FAILED);
    }

    // ===========================
    // LEGACY METHODS (for backward compatibility)
    // ===========================

    // Keep some legacy methods for backward compatibility
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .data(data)
                .message(message)
                .build();
    }

    public static ApiResponse<Void> error(HttpStatus status, @NonNull String message) {
        return ApiResponse.<Void>builder()
                .status(status.value())
                .message(message)
                .build();
    }

    public static ApiResponse<Void> error(int status, @NonNull String message) {
        return ApiResponse.<Void>builder()
                .status(status)
                .message(message)
                .build();
    }

    // ===========================
    // BUILDER METHODS
    // ===========================

    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }

    public ApiResponse<T> withMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public ApiResponse<T> withMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    // ===========================
    // UTILITY METHODS
    // ===========================

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }

    public boolean isError() {
        return !isSuccess();
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}