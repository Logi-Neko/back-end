package exe2.learningapp.logineko.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =========================
    // AUTHENTICATION
    // =========================
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "AUTH_INVALID_CREDENTIALS", "Tên đăng nhập hoặc mật khẩu không hợp lệ."),
    AUTH_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "AUTH_INVALID_PASSWORD", "Mật khẩu không hợp lệ."),
    AUTH_ACCOUNT_LOCKED(HttpStatus.LOCKED.value(), "AUTH_ACCOUNT_LOCKED", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên."),
    AUTH_SUCCESS(HttpStatus.OK.value(), "AUTH_SUCCESS", "Đăng nhập thành công."),
    AUTH_FAILURE(HttpStatus.UNAUTHORIZED.value(), "AUTH_FAILURE", "Đăng nhập không thành công. Vui lòng thử lại."),
    AUTH_LOGOUT_SUCCESS(HttpStatus.OK.value(), "AUTH_LOGOUT_SUCCESS", "Đăng xuất thành công."),
    AUTH_SESSION_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "AUTH_SESSION_EXPIRED", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."),
    AUTH_OTP_REQUIRED(HttpStatus.UNAUTHORIZED.value(), "AUTH_OTP_REQUIRED", "Vui lòng nhập mã OTP để xác thực."),
    AUTH_OTP_INVALID(HttpStatus.UNAUTHORIZED.value(), "AUTH_OTP_INVALID", "Mã OTP không hợp lệ hoặc đã hết hạn."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED.value(), "REFRESH_TOKEN_INVALID", "Token làm mới không hợp lệ hoặc đã hết hạn."),

    // =========================
    // REGISTRATION
    // =========================
    REG_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST.value(), "REG_EMAIL_REQUIRED", "Email là bắt buộc."),
    REG_EMAIL_INVALID(HttpStatus.BAD_REQUEST.value(), "REG_EMAIL_INVALID", "Email không hợp lệ."),
    REG_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST.value(), "REG_PASSWORD_MISMATCH", "Mật khẩu không khớp. Vui lòng thử lại."),
    REG_USERNAME_TAKEN(HttpStatus.CONFLICT.value(), "REG_USERNAME_TAKEN", "Tên đăng nhập đã tồn tại."),
    REG_EMAIL_TAKEN(HttpStatus.CONFLICT.value(), "REG_EMAIL_TAKEN", "Email đã được sử dụng."),
    REG_SUCCESS(HttpStatus.CREATED.value(), "REG_SUCCESS", "Đăng ký tài khoản thành công."),
    REG_FAILURE(HttpStatus.BAD_REQUEST.value(), "REG_FAILURE", "Đăng ký không thành công. Vui lòng thử lại."),

    // =========================
    // GENERAL
    // =========================
    GEN_ACTION_SUCCESS(HttpStatus.OK.value(), "GEN_ACTION_SUCCESS", "Hành động đã thành công."),
    GEN_ACTION_FAILURE(HttpStatus.BAD_REQUEST.value(), "GEN_ACTION_FAILURE", "Hành động không thành công. Vui lòng thử lại."),
    GEN_NO_CONTENT(HttpStatus.NO_CONTENT.value(), "GEN_NO_CONTENT", "Không có nội dung để hiển thị."),
    GEN_CREATED_SUCCESS(HttpStatus.CREATED.value(), "GEN_CREATED_SUCCESS", "Tài nguyên đã được tạo thành công."),
    GEN_UPDATED_SUCCESS(HttpStatus.OK.value(), "GEN_UPDATED_SUCCESS", "Tài nguyên đã được cập nhật thành công."),
    GEN_DELETED_SUCCESS(HttpStatus.OK.value(), "GEN_DELETED_SUCCESS", "Tài nguyên đã được xóa thành công."),
    GEN_RETRIEVED_SUCCESS(HttpStatus.OK.value(), "GEN_RETRIEVED_SUCCESS", "Tài nguyên đã được lấy thành công."),
    GEN_OPERATION_NOT_ALLOWED(HttpStatus.FORBIDDEN.value(), "GEN_OPERATION_NOT_ALLOWED", "Thao tác này không được phép."),

    // =========================
    // ERRORS
    // =========================
    ERR_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "ERR_NOT_FOUND", "Không tìm thấy tài nguyên yêu cầu."),
    ERR_EXISTS(HttpStatus.CONFLICT.value(), "ERR_EXISTS", "Tài nguyên đã tồn tại."),
    ERR_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERR_SERVER_ERROR", "Lỗi máy chủ. Vui lòng thử lại sau."),
    ERR_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "ERR_BAD_REQUEST", "Yêu cầu không hợp lệ."),
    ERR_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "ERR_FORBIDDEN", "Bạn không có quyền thực hiện thao tác này."),
    ERR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "ERR_UNAUTHORIZED", "Chưa được xác thực. Vui lòng đăng nhập."),
    ERR_TIMEOUT(HttpStatus.REQUEST_TIMEOUT.value(), "ERR_TIMEOUT", "Yêu cầu quá thời gian. Vui lòng thử lại."),
    INVALID_END_DATE(HttpStatus.BAD_REQUEST.value(), "ERR_END_DATE", "Ngày kết thúc phải sau ngày bắt đầu."),
    ERR_INACTIVE(HttpStatus.BAD_REQUEST.value(), "ERR_INACTIVE", "Tài nguyên không còn hoạt động."),
    // =========================
    // PERMISSIONS
    // =========================
    PERM_DENIED(HttpStatus.FORBIDDEN.value(), "PERM_DENIED", "Bạn không có quyền truy cập vào tài nguyên này."),
    PERM_GRANTED(HttpStatus.OK.value(), "PERM_GRANTED", "Quyền truy cập đã được cấp thành công."),
    PERM_AUTH_REQUIRED(HttpStatus.UNAUTHORIZED.value(), "PERM_AUTH_REQUIRED", "Bạn cần đăng nhập để thực hiện hành động này."),
    PERM_ROLE_REQUIRED(HttpStatus.FORBIDDEN.value(), "PERM_ROLE_REQUIRED", "Bạn không có vai trò phù hợp để truy cập."),
    PERM_ACCESS_RESTRICTED(HttpStatus.FORBIDDEN.value(), "PERM_ACCESS_RESTRICTED", "Truy cập bị giới hạn."),

    // =========================
    // VALIDATION
    // =========================
    VAL_FIELD_REQUIRED(HttpStatus.BAD_REQUEST.value(), "VAL_FIELD_REQUIRED", "Trường {0} là bắt buộc."),
    VAL_FIELD_INVALID(HttpStatus.BAD_REQUEST.value(), "VAL_FIELD_INVALID", "Trường {0} không hợp lệ."),
    VAL_FIELD_MIN_LENGTH(HttpStatus.BAD_REQUEST.value(), "VAL_FIELD_MIN_LENGTH", "Trường {0} phải có ít nhất {1} ký tự."),
    VAL_FIELD_MAX_LENGTH(HttpStatus.BAD_REQUEST.value(), "VAL_FIELD_MAX_LENGTH", "Trường {0} không được vượt quá {1} ký tự."),
    VAL_FIELD_EMAIL_INVALID(HttpStatus.BAD_REQUEST.value(), "VAL_FIELD_EMAIL_INVALID", "Trường {0} phải là email hợp lệ."),
    VAL_FIELD_NUMBER_INVALID(HttpStatus.BAD_REQUEST.value(), "VAL_FIELD_NUMBER_INVALID", "Trường {0} phải là số hợp lệ."),

    // =========================
    // NOTIFICATIONS
    // =========================
    NOTIF_SENT(HttpStatus.OK.value(), "NOTIF_SENT", "Thông báo đã được gửi thành công."),
    NOTIF_FAILED(HttpStatus.BAD_REQUEST.value(), "NOTIF_FAILED", "Gửi thông báo không thành công. Vui lòng thử lại."),
    NOTIF_NEW_MESSAGE(HttpStatus.OK.value(), "NOTIF_NEW_MESSAGE", "Bạn có tin nhắn mới."),
    NOTIF_SYSTEM_ANNOUNCE(HttpStatus.OK.value(), "NOTIF_SYSTEM_ANNOUNCE", "Có thông báo hệ thống mới."),

    // =========================
    // CONFIRMATION
    // =========================
    CONF_REQUIRED(HttpStatus.BAD_REQUEST.value(), "CONF_REQUIRED", "Vui lòng xác nhận hành động này."),
    CONF_SUCCESS(HttpStatus.OK.value(), "CONF_SUCCESS", "Hành động đã được xác nhận thành công."),
    CONF_CANCELLED(HttpStatus.OK.value(), "CONF_CANCELLED", "Hành động đã bị hủy."),

    // =========================
    // UPLOADS
    // =========================
    UPLOAD_SUCCESS(HttpStatus.OK.value(), "UPLOAD_SUCCESS", "Tệp đã được tải lên thành công."),
    UPLOAD_FAILURE(HttpStatus.BAD_REQUEST.value(), "UPLOAD_FAILURE", "Tải tệp lên không thành công. Vui lòng thử lại."),
    UPLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE.value(), "UPLOAD_TOO_LARGE", "Kích thước tệp vượt quá giới hạn cho phép."),
    UPLOAD_TYPE_NOT_SUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "UPLOAD_TYPE_NOT_SUPPORTED", "Định dạng tệp không được hỗ trợ."),
    UPLOAD_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "UPLOAD_NOT_FOUND", "Không tìm thấy tệp yêu cầu."),

    // =========================
    // SYSTEM
    // =========================
    SYS_BUSY(HttpStatus.SERVICE_UNAVAILABLE.value(), "SYS_BUSY", "Hệ thống đang bận. Vui lòng thử lại sau."),
    SYS_MAINTENANCE(HttpStatus.SERVICE_UNAVAILABLE.value(), "SYS_MAINTENANCE", "Hệ thống đang bảo trì. Vui lòng quay lại sau."),
    SYS_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "SYS_UNAVAILABLE", "Dịch vụ hiện không khả dụng."),

    // =========================
    // KEYCLOAK
    // =========================
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "UNCATEGORIZED_EXCEPTION",
            "Lỗi không xác định"),
    INVALID_KEY(HttpStatus.BAD_REQUEST.value(),
            "INVALID_KEY",
            "Khóa không hợp lệ"),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST.value(),
            "INVALID_USERNAME",
            "Tên đăng nhập phải có ít nhất {0} ký tự"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(),
            "INVALID_PASSWORD",
            "Mật khẩu phải có ít nhất {0} ký tự"),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED.value(),
            "UNAUTHENTICATED",
            "Chưa được xác thực"),
    UNAUTHORIZED(HttpStatus.FORBIDDEN.value(),
            "UNAUTHORIZED",
            "Bạn không có quyền thực hiện hành động này"),
    EMAIL_EXISTED(HttpStatus.BAD_REQUEST.value(),
            "EMAIL_EXISTED",
            "Email đã tồn tại, vui lòng chọn email khác"),
    USER_EXISTED(HttpStatus.BAD_REQUEST.value(),
            "USER_EXISTED",
            "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác"),
    USERNAME_IS_MISSING(HttpStatus.BAD_REQUEST.value(),
            "USERNAME_IS_MISSING",
            "Vui lòng nhập tên đăng nhập"),

    ERR_INVALID_INPUT(HttpStatus.BAD_REQUEST.value(),
            "INVALID_AGE_INPUT",
                    "Tuổi của bé từ 0 đến 18 tuổi"),
    ERR_INVALID_GENDER(HttpStatus.BAD_REQUEST.value(),
            "INVALID_GENDER",
            "Giới tính không hợp lệ")

    ;


    private final int status;
    private final String code;
    private final String message;
}
