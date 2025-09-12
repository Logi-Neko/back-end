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
public class SubscriptionController {

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

    @GetMapping("/account/{accountId}")
    @Operation(
            summary = "Lấy đăng ký theo tài khoản",
            description = "Lấy danh sách tất cả đăng ký của một tài khoản cụ thể"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy đăng ký theo tài khoản thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    public ApiResponse<List<SubscriptionDto>> getSubscriptionsByAccount(
            @Parameter(description = "ID của tài khoản") @PathVariable Long accountId) {
        log.info("Getting subscriptions for account ID: {}", accountId);

        List<SubscriptionDto> subscriptions = subscriptionService.getSubscriptionsByAccountId(accountId);
        return ApiResponse.success(subscriptions, "Lấy đăng ký theo tài khoản thành công");
    }

    @GetMapping("/account/{accountId}/active")
    @Operation(
            summary = "Lấy đăng ký đang hoạt động",
            description = "Lấy đăng ký đang hoạt động của tài khoản (chưa hết hạn và trạng thái ACTIVE)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy đăng ký hoạt động thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản hoặc đăng ký hoạt động")
    })
    public ApiResponse<SubscriptionDto> getActiveSubscriptionByAccount(
            @Parameter(description = "ID của tài khoản") @PathVariable Long accountId) {
        log.info("Getting active subscription for account ID: {}", accountId);

        SubscriptionDto subscription = subscriptionService.getActiveSubscriptionByAccountId(accountId);
        return ApiResponse.success(subscription, "Lấy đăng ký hoạt động thành công");
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Lấy đăng ký theo trạng thái",
            description = "Lấy danh sách đăng ký theo trạng thái cụ thể (ACTIVE, INACTIVE, EXPIRED)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy đăng ký theo trạng thái thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ")
    })
    public ApiResponse<List<SubscriptionDto>> getSubscriptionsByStatus(
            @Parameter(description = "Trạng thái đăng ký (ACTIVE, INACTIVE, EXPIRED)")
            @PathVariable SubscriptionStatus status) {
        log.info("Getting subscriptions by status: {}", status);

        List<SubscriptionDto> subscriptions = subscriptionService.getSubscriptionsByStatus(status);
        return ApiResponse.success(subscriptions, "Lấy đăng ký theo trạng thái thành công");
    }

    @GetMapping("/account/{accountId}/summary")
    @Operation(
            summary = "Lấy tóm tắt đăng ký",
            description = "Lấy thông tin tóm tắt các đăng ký của tài khoản bao gồm trạng thái và thời gian"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy tóm tắt đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    public ApiResponse<List<SubscriptionSummaryDto>> getSubscriptionSummaryByAccount(
            @Parameter(description = "ID của tài khoản") @PathVariable Long accountId) {
        log.info("Getting subscription summary for account ID: {}", accountId);

        List<SubscriptionSummaryDto> summary = subscriptionService.getSubscriptionSummaryByAccountId(accountId);
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

    @PatchMapping("/{id}/activate")
    @Operation(
            summary = "Kích hoạt đăng ký",
            description = "Kích hoạt lại đăng ký bằng cách thay đổi trạng thái thành ACTIVE"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kích hoạt đăng ký thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đăng ký"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Đăng ký đã được kích hoạt trước đó")
    })
    public ApiResponse<SubscriptionDto> activateSubscription(
            @Parameter(description = "ID của đăng ký") @PathVariable Long id) {
        log.info("Activating subscription with ID: {}", id);

        SubscriptionDto subscription = subscriptionService.activateSubscription(id);
        return ApiResponse.success(subscription, "Kích hoạt đăng ký thành công");
    }

    @GetMapping("/{id}/days-remaining")
    @Operation(
            summary = "Tính số ngày còn lại",
            description = "Tính số ngày còn lại của đăng ký dựa trên ngày kết thúc"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tính số ngày còn lại thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy đăng ký")
    })
    public ApiResponse<Long> getDaysRemaining(
            @Parameter(description = "ID của đăng ký") @PathVariable Long id) {
        log.info("Calculating days remaining for subscription ID: {}", id);

        long daysRemaining = subscriptionService.calculateDaysRemaining(id);
        return ApiResponse.success(daysRemaining, "Tính số ngày còn lại thành công");
    }

    @GetMapping("/revenue/by-type")
    @Operation(
            summary = "Tổng doanh thu theo loại gói",
            description = "Tính tổng doanh thu từ tất cả đăng ký của loại gói cụ thể"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tính doanh thu theo loại thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Loại gói không hợp lệ")
    })
    public ApiResponse<Double> getRevenueByType(
            @Parameter(description = "Loại gói đăng ký") @RequestParam String type) {
        log.info("Getting total revenue for type: {}", type);

        double revenue = subscriptionService.getTotalRevenueByType(type);
        return ApiResponse.success(revenue, "Tính doanh thu theo loại thành công");
    }

    @GetMapping("/revenue/between")
    @Operation(
            summary = "Doanh thu theo khoảng thời gian",
            description = "Tính tổng doanh thu từ các đăng ký được tạo trong khoảng thời gian cụ thể"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tính doanh thu theo khoảng thời gian thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Khoảng thời gian không hợp lệ")
    })
    public ApiResponse<Double> getRevenueBetween(
            @Parameter(description = "Ngày bắt đầu") @RequestParam LocalDate startDate,
            @Parameter(description = "Ngày kết thúc") @RequestParam LocalDate endDate) {
        log.info("Getting total revenue between {} and {}", startDate, endDate);

        double revenue = subscriptionService.getTotalRevenueBetween(startDate, endDate);
        return ApiResponse.success(revenue, "Tính doanh thu theo khoảng thời gian thành công");
    }

    @GetMapping("/count/by-status/{status}")
    @Operation(
            summary = "Đếm đăng ký theo trạng thái",
            description = "Đếm tổng số lượng đăng ký theo trạng thái cụ thể"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đếm đăng ký theo trạng thái thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Trạng thái không hợp lệ")
    })
    public ApiResponse<Long> countSubscriptionsByStatus(
            @Parameter(description = "Trạng thái đăng ký") @PathVariable SubscriptionStatus status) {
        log.info("Counting subscriptions by status: {}", status);

        long count = subscriptionService.countSubscriptionsByStatus(status);
        return ApiResponse.success(count, "Đếm đăng ký theo trạng thái thành công");
    }

    @GetMapping("/count/active")
    @Operation(
            summary = "Đếm đăng ký hoạt động",
            description = "Đếm số lượng đăng ký đang hoạt động và chưa hết hạn"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đếm đăng ký hoạt động thành công")
    })
    public ApiResponse<Long> countActiveSubscriptions() {
        log.info("Counting active subscriptions");

        long count = subscriptionService.countActiveSubscriptions();
        return ApiResponse.success(count, "Đếm đăng ký hoạt động thành công");
    }

    @PatchMapping("/update-expired")
    @Operation(
            summary = "Cập nhật đăng ký hết hạn",
            description = "Tự động cập nhật trạng thái các đăng ký đã hết hạn thành EXPIRED"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật đăng ký hết hạn thành công")
    })
    public ApiResponse<Void> updateExpiredSubscriptions() {
        log.info("Updating expired subscriptions");

        subscriptionService.updateExpiredSubscriptions();
        return ApiResponse.success(null, "Cập nhật đăng ký hết hạn thành công");
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

    @DeleteMapping("/account/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Xóa đăng ký theo tài khoản",
            description = "Xóa tất cả đăng ký của một tài khoản. Thao tác này không thể hoàn tác"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa đăng ký theo tài khoản thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản")
    })
    public ApiResponse<Void> deleteSubscriptionsByAccount(
            @Parameter(description = "ID của tài khoản") @PathVariable Long accountId) {
        log.info("Deleting subscriptions for account ID: {}", accountId);

        subscriptionService.deleteSubscriptionsByAccountId(accountId);
        return ApiResponse.success(null, "Xóa đăng ký theo tài khoản thành công");
    }
}