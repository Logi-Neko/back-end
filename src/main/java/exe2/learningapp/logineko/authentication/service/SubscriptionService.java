package exe2.learningapp.logineko.authentication.service;

import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionCreateDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionSummaryDto;
import exe2.learningapp.logineko.authentication.dtos.subcription.SubscriptionUpdateDto;
import exe2.learningapp.logineko.authentication.entity.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionService {

    // Tạo đăng ký mới
    SubscriptionDto createSubscription(SubscriptionCreateDto subscriptionCreateDto);

    // Cập nhật thông tin đăng ký
    SubscriptionDto updateSubscription(Long id, SubscriptionUpdateDto subscriptionUpdateDto);

    // Lấy thông tin đăng ký theo ID
    SubscriptionDto getSubscriptionById(Long id);

    // Lấy tất cả đăng ký
    List<SubscriptionDto> getAllSubscriptions();

    // Lấy đăng ký theo tài khoản
    List<SubscriptionDto> getSubscriptionsByAccountId(Long accountId);

    // Lấy đăng ký đang hoạt động theo tài khoản
    SubscriptionDto getActiveSubscriptionByAccountId(Long accountId);

    // Lấy đăng ký theo trạng thái
    List<SubscriptionDto> getSubscriptionsByStatus(SubscriptionStatus status);

    // Lấy tóm tắt đăng ký theo tài khoản
    List<SubscriptionSummaryDto> getSubscriptionSummaryByAccountId(Long accountId);

    // Gia hạn đăng ký
    SubscriptionDto renewSubscription(Long id, LocalDate newEndDate);

    // Hủy đăng ký
    SubscriptionDto cancelSubscription(Long id);

    SubscriptionDto activateSubscription(Long id);

    // Tính số ngày còn lại của đăng ký
    long calculateDaysRemaining(Long subscriptionId);

    // Lấy tổng doanh thu theo loại đăng ký
    double getTotalRevenueByType(String type);

    // Lấy tổng doanh thu trong khoảng thời gian
    double getTotalRevenueBetween(LocalDate startDate, LocalDate endDate);

    // Đếm số đăng ký theo trạng thái
    long countSubscriptionsByStatus(SubscriptionStatus status);

    // Đếm số đăng ký hoạt động
    long countActiveSubscriptions();

    // Xóa đăng ký
    void deleteSubscription(Long id);

    // Xóa đăng ký theo tài khoản
    void deleteSubscriptionsByAccountId(Long accountId);

    // Cập nhật trạng thái đăng ký hết hạn tự động
    void updateExpiredSubscriptions();

    // Lấy thống kê đăng ký theo tháng
    List<Object[]> getSubscriptionStatsByMonth(int year);
}