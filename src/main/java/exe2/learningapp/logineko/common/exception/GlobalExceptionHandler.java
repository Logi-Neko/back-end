package exe2.learningapp.logineko.common.exception;

import exe2.learningapp.logineko.common.ApiResponse;
import exe2.learningapp.logineko.common.util.MessageFormatter;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Your main exception handler
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(AppException ex, HttpServletRequest request) {
        log.warn("API Exception: {} - {}", ex.getErrorCode().getCode(), ex.getMessage());

        ErrorCode error = ex.getErrorCode();
        String message = MessageFormatter.format(error);

        ApiResponse<?> response = ApiResponse.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(response);
    }

    // Handle Keycloak/Feign exceptions (for your 401 issue)
//    @ExceptionHandler(FeignException.class)
//    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException ex, HttpServletRequest request) {
//        log.error("Feign Exception: {} - {}", ex.status(), ex.getMessage());
//
//        ErrorCode errorCode;
//
//        // Map common Feign exceptions to your ErrorCodes
//        switch (ex.status()) {
//            case 400:
//                errorCode = ErrorCode.ERR_BAD_REQUEST;
//                break;
//            case 401:
//                // Check if it's Keycloak token exchange
//                if (ex.request() != null && ex.request().url().contains("/protocol/openid-connect/token")) {
//                    errorCode = ErrorCode.AUTH_INVALID_CREDENTIALS;
//                } else {
//                    errorCode = ErrorCode.ERR_UNAUTHORIZED;
//                }
//                break;
//            case 403:
//                errorCode = ErrorCode.ERR_FORBIDDEN;
//                break;
//            case 404:
//                errorCode = ErrorCode.ERR_NOT_FOUND;
//                break;
//            case 408:
//                errorCode = ErrorCode.ERR_TIMEOUT;
//                break;
//            case 500:
//            default:
//                errorCode = ErrorCode.ERR_SERVER_ERROR;
//                break;
//        }
//
//        ApiResponse<?> response = ApiResponse.builder()
//                .status(errorCode.getStatus())
//                .code(errorCode.getCode())
//                .message(errorCode.getMessage())
//                .path(request.getRequestURI())
//                .build();
//
//        return ResponseEntity.status(errorCode.getStatus()).body(response);
//    }

    // Handle validation errors
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<?>> handleValidationException(Exception ex, HttpServletRequest request) {
        log.warn("Validation Exception: {}", ex.getMessage());

        List<String> errors;

        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationEx = (MethodArgumentNotValidException) ex;
            errors = validationEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
        } else if (ex instanceof BindException) {
            BindException bindEx = (BindException) ex;
            errors = bindEx.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
        } else {
            errors = List.of(ex.getMessage());
        }

        ErrorCode errorCode = ErrorCode.ERR_BAD_REQUEST;

        ApiResponse<?> response = ApiResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Uncaught Exception: {}", ex.getMessage(), ex);

        ErrorCode error = ErrorCode.ERR_SERVER_ERROR;

        ApiResponse<?> response = ApiResponse.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(error.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(response);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Entity Not Found Exception: {}", ex.getMessage());

        ErrorCode error = ErrorCode.ERR_NOT_FOUND;

        ApiResponse<?> response = ApiResponse.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(response);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityExistsException(EntityExistsException ex, HttpServletRequest request) {
        log.warn("Entity Exists Exception: {}", ex.getMessage());

        ErrorCode error = ErrorCode.ERR_EXISTS;

        ApiResponse<?> response = ApiResponse.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access Denied Exception: {}", ex.getMessage());

        ErrorCode error = ErrorCode.ERR_FORBIDDEN;

        ApiResponse<?> response = ApiResponse.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(error.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(error.getStatus()).body(response);
    }
}