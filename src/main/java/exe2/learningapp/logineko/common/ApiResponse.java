package exe2.learningapp.logineko.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
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
    String message;
    T data;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    LocalDateTime timestamp;
    String path;
    List<String> errors;
    Map<String, Object> metadata;

    // Success responses
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .data(data)
                .message(message)
//                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "{action_success}");
    }

    public static ApiResponse<Void> success(String message) {
        return success(null, message);
    }

    public static ApiResponse<Void> success() {
        return success("{action_success}");
    }

    // Created response (201)
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .data(data)
                .message(message)
//                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return created(data, "{created_success}");
    }

    // No Content response (204)
    public static ApiResponse<Void> noContent() {
        return ApiResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("{no_content}")
//                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error responses
    public static ApiResponse<Void> error(HttpStatus status, @NonNull String message) {
        return ApiResponse.<Void>builder()
                .status(status.value())
                .message(message)
//                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> error(int status, @NonNull String message) {
        return ApiResponse.<Void>builder()
                .status(status)
                .message(message)
//                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error with validation errors
    public static ApiResponse<Void> error(HttpStatus status, @NonNull String message, List<String> errors) {
        return ApiResponse.<Void>builder()
                .status(status.value())
                .message(message)
                .errors(errors)
//                .timestamp(LocalDateTime.now())
                .build();
    }

    // Common error responses
    public static ApiResponse<Void> badRequest(@NonNull String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiResponse<Void> badRequest(@NonNull String message, List<String> errors) {
        return error(HttpStatus.BAD_REQUEST, message, errors);
    }

    public static ApiResponse<Void> unauthorized(@NonNull String message) {
        return error(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiResponse<Void> unauthorized() {
        return unauthorized("{authentication_required}");
    }

    public static ApiResponse<Void> forbidden(@NonNull String message) {
        return error(HttpStatus.FORBIDDEN, message);
    }

    public static ApiResponse<Void> forbidden() {
        return forbidden("{permission_denied}");
    }

    public static ApiResponse<Void> notFound(@NonNull String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    public static ApiResponse<Void> notFound() {
        return notFound("{not_found}");
    }

    public static ApiResponse<Void> conflict(@NonNull String message) {
        return error(HttpStatus.CONFLICT, message);
    }

    public static ApiResponse<Void> internalServerError(@NonNull String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static ApiResponse<Void> internalServerError() {
        return internalServerError("{server_error}");
    }

    // Builder methods for additional context
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

    // Utility methods
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