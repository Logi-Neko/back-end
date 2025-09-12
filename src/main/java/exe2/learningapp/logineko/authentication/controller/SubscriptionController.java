package exe2.learningapp.logineko.authentication.controller;

import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionCreateDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionSummaryDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionUpdateDto;
import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import exe2.learningapp.logineko.authentication.service.SubscriptionService;
import exe2.learningapp.logineko.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscription Management", description = "API quản lý đăng ký gói dịch vụ trong hệ thống")
public class  SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Tạo đăng ký mới",
            description = "Tạo một đăng ký gói dịch vụ mới cho tài khoản với thông tin như loại gói, thời gian và giá cả"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin đăng ký không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Tài khoản đã có đăng ký hoạt động")
    })
    public ApiResponse<SubscriptionDto> createSubscription(
            @Valid @RequestBody SubscriptionCreateDto subscriptionCreateDto) {
        log.info("Creating subscription for account ID: {}", subscriptionCreateDto.accountId());

        SubscriptionDto subscription = subscriptionService.createSubscription(subscriptionCreateDto);
        return ApiResponse.success(subscription, "Tạo đăng ký thành công");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Cập nhật thông tin đăng ký",
            description = "Cập nhật toàn bộ thông tin đăng ký bao gồm loại gói, thời gian và giá cả"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đăng ký"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Thông tin cập nhật không hợp lệ")
    })
    public ApiResponse<SubscriptionDto> updateSubscription(
            @Parameter(description = "ID của đăng ký") @PathVariable Long id,
            @Valid @RequestBody SubscriptionUpdateDto subscriptionUpdateDto) {
        log.info("Updating subscription with ID: {}", id);

        SubscriptionDto subscription = subscriptionService.updateSubscription(id, subscriptionUpdateDto);
        return ApiResponse.success(subscription, "Cập nhật đăng ký thành công");
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thông tin đăng ký theo ID",
            description = "Lấy thông tin chi tiết của một đăng ký cụ thể dựa trên ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đăng ký")
    })
    public ApiResponse<SubscriptionDto> getSubscription(
            @Parameter(description = "ID của đăng ký") @PathVariable Long id) {
        log.info("Getting subscription by ID: {}", id);

        SubscriptionDto subscription = subscriptionService.getSubscriptionById(id);
        return ApiResponse.success(subscription, "Lấy thông tin đăng ký thành công");
    }

    @GetMapping
    @Operation(
            summary = "Lấy tất cả đăng ký",
            description = "Lấy danh sách tất cả đăng ký có trong hệ thống, bao gồm cả đăng ký đã hết hạn"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách đăng ký thành công")
    })
    public ApiResponse<List<SubscriptionDto>> getAllSubscriptions() {
        log.info("Getting all subscriptions");

        List<SubscriptionDto> subscriptions = subscriptionService.getAllSubscriptions();
        return ApiResponse.success(subscriptions, "Lấy danh sách tất cả đăng ký thành công");
    }

    @GetMapping("/account/active")
    @Operation(
            summary = "Lấy đăng ký đang hoạt động",
            description = "Lấy đăng ký đang hoạt động của tài khoản (chưa hết hạn và trạng thái ACTIVE)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy đăng ký hoạt động thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản hoặc đăng ký hoạt động")
    })
    public ApiResponse<SubscriptionDto> getActiveSubscriptionByAccount() {
        SubscriptionDto subscription = subscriptionService.getActiveSubscriptionByAccountId();
        return ApiResponse.success(subscription, "Lấy đăng ký hoạt động thành công");
    }

    @GetMapping("/account/summary")
    @Operation(
            summary = "Lấy tóm tắt đăng ký",
            description = "Lấy thông tin tóm tắt các đăng ký của tài khoản bao gồm trạng thái và thời gian"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy tóm tắt đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    public ApiResponse<List<SubscriptionSummaryDto>> getSubscriptionSummaryByAccount() {
        List<SubscriptionSummaryDto> summary = subscriptionService.getSubscriptionSummaryByAccountId();
        return ApiResponse.success(summary, "Lấy tóm tắt đăng ký thành công");
    }

    @PatchMapping("/{id}/renew")
    @Operation(
            summary = "Gia hạn đăng ký",
            description = "Gia hạn đăng ký với ngày kết thúc mới và tự động kích hoạt lại"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gia hạn đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đăng ký"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ngày kết thúc mới không hợp lệ")
    })
    public ApiResponse<SubscriptionDto> renewSubscription(
            @Parameter(description = "ID của đăng ký") @PathVariable Long id,
            @Parameter(description = "Ngày kết thúc mới") @RequestParam LocalDate newEndDate) {
        log.info("Renewing subscription ID: {} with new end date: {}", id, newEndDate);

        SubscriptionDto subscription = subscriptionService.renewSubscription(id, newEndDate);
        return ApiResponse.success(subscription, "Gia hạn đăng ký thành công");
    }

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Hủy đăng ký",
            description = "Hủy đăng ký bằng cách thay đổi trạng thái thành INACTIVE"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hủy đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đăng ký"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Đăng ký đã được hủy trước đó")
    })
    public ApiResponse<SubscriptionDto> cancelSubscription(
            @Parameter(description = "ID của đăng ký") @PathVariable Long id) {
        log.info("Cancelling subscription with ID: {}", id);

        SubscriptionDto subscription = subscriptionService.cancelSubscription(id);
        return ApiResponse.success(subscription, "Hủy đăng ký thành công");
    }

    @GetMapping("/statistics/monthly/{year}")
    @Operation(
            summary = "Thống kê đăng ký theo tháng",
            description = "Lấy thống kê số lượng và doanh thu đăng ký theo từng tháng trong năm"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thống kê theo tháng thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Năm không hợp lệ")
    })
    public ApiResponse<List<Object[]>> getMonthlyStatistics(
            @Parameter(description = "Năm thống kê") @PathVariable int year) {
        log.info("Getting subscription statistics for year: {}", year);

        List<Object[]> statistics = subscriptionService.getSubscriptionStatsByMonth(year);
        return ApiResponse.success(statistics, "Lấy thống kê theo tháng thành công");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa đăng ký vĩnh viễn",
            description = "Xóa hoàn toàn đăng ký khỏi hệ thống. Thao tác này không thể hoàn tác"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đăng ký")
    })
    public ApiResponse<Void> deleteSubscription(
            @Parameter(description = "ID của đăng ký") @PathVariable Long id) {
        log.info("Deleting subscription with ID: {}", id);

        subscriptionService.deleteSubscription(id);
        return ApiResponse.success(null, "Xóa đăng ký thành công");
    }

    @GetMapping("/active")
    @Operation(
            summary = "Kiểm tra trạng thái đăng ký hoạt động",
            description = "Kiểm tra xem tài khoản hiện tại có đăng ký hoạt động hay không"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra trạng thái đăng ký thành công")
    })
    public ApiResponse<Boolean> isSubscriptionActive() {
        boolean isActive = subscriptionService.isSubscriptionActive();
        return ApiResponse.success(isActive, "Kiểm tra trạng thái đăng ký thành công");
    }

}