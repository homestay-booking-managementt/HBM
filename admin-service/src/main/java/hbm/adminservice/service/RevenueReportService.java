package hbm.adminservice.service;

import hbm.adminservice.dto.RevenueReportDTO;
import hbm.adminservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RevenueReportService {
    
    private final BookingRepository bookingRepository;
    
    /**
     * Lấy báo cáo doanh thu tổng hợp
     */
    public RevenueReportDTO getRevenueReport() {
        RevenueReportDTO report = new RevenueReportDTO();
        
        // Tổng quan booking
        Long totalBookings = bookingRepository.count();
        report.setTotalBookings(totalBookings);
        
        // Đếm booking theo trạng thái
        Long completedBookings = bookingRepository.countByStatus("completed");
        Long cancelledBookings = bookingRepository.countByStatus("cancelled");
        Long pendingBookings = bookingRepository.countByStatus("pending");
        Long confirmedBookings = bookingRepository.countByStatus("confirmed");
        
        report.setCompletedBookings(completedBookings);
        report.setCancelledBookings(cancelledBookings);
        report.setPendingBookings(pendingBookings);
        
        // Tính tổng doanh thu (tất cả các trạng thái trừ cancelled)
        BigDecimal totalRevenue = bookingRepository.sumTotalPriceByStatusNotIn("cancelled");
        report.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        // Doanh thu theo trạng thái
        BigDecimal completedRevenue = bookingRepository.sumTotalPriceByStatus("completed");
        BigDecimal pendingRevenue = bookingRepository.sumTotalPriceByStatus("pending");
        BigDecimal confirmedRevenue = bookingRepository.sumTotalPriceByStatus("confirmed");
        
        report.setCompletedRevenue(completedRevenue != null ? completedRevenue : BigDecimal.ZERO);
        report.setPendingRevenue(pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO);
        report.setConfirmedRevenue(confirmedRevenue != null ? confirmedRevenue : BigDecimal.ZERO);
        
        // Giá trị trung bình mỗi đơn
        if (totalBookings > 0 && report.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal average = report.getTotalRevenue()
                    .divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP);
            report.setAverageBookingValue(average);
        } else {
            report.setAverageBookingValue(BigDecimal.ZERO);
        }
        
        // Số homestay có booking
        Long totalHomestays = bookingRepository.countDistinctHomestayId();
        report.setTotalHomestays(totalHomestays);
        
        // Số khách hàng
        Long totalCustomers = bookingRepository.countDistinctUserId();
        report.setTotalCustomers(totalCustomers);
        
        // Thời gian tạo báo cáo
        report.setReportGeneratedAt(LocalDateTime.now());
        report.setPeriod("Toàn bộ thời gian");
        
        return report;
    }
    
    /**
     * Lấy báo cáo doanh thu theo khoảng thời gian
     */
    public RevenueReportDTO getRevenueReportByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        RevenueReportDTO report = new RevenueReportDTO();
        
        // Tổng số booking trong khoảng thời gian
        Long totalBookings = bookingRepository.countByCreatedAtBetween(startDate, endDate);
        report.setTotalBookings(totalBookings);
        
        // Đếm booking theo trạng thái trong khoảng thời gian
        Long completedBookings = bookingRepository.countByStatusAndCreatedAtBetween("completed", startDate, endDate);
        Long cancelledBookings = bookingRepository.countByStatusAndCreatedAtBetween("cancelled", startDate, endDate);
        Long pendingBookings = bookingRepository.countByStatusAndCreatedAtBetween("pending", startDate, endDate);
        
        report.setCompletedBookings(completedBookings);
        report.setCancelledBookings(cancelledBookings);
        report.setPendingBookings(pendingBookings);
        
        // Tính tổng doanh thu (trừ cancelled) trong khoảng thời gian
        BigDecimal totalRevenue = bookingRepository.sumTotalPriceByStatusNotInAndCreatedAtBetween("cancelled", startDate, endDate);
        report.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        // Doanh thu theo trạng thái trong khoảng thời gian
        BigDecimal completedRevenue = bookingRepository.sumTotalPriceByStatusAndCreatedAtBetween("completed", startDate, endDate);
        BigDecimal pendingRevenue = bookingRepository.sumTotalPriceByStatusAndCreatedAtBetween("pending", startDate, endDate);
        BigDecimal confirmedRevenue = bookingRepository.sumTotalPriceByStatusAndCreatedAtBetween("confirmed", startDate, endDate);
        
        report.setCompletedRevenue(completedRevenue != null ? completedRevenue : BigDecimal.ZERO);
        report.setPendingRevenue(pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO);
        report.setConfirmedRevenue(confirmedRevenue != null ? confirmedRevenue : BigDecimal.ZERO);
        
        // Giá trị trung bình mỗi đơn
        if (totalBookings > 0 && report.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal average = report.getTotalRevenue()
                    .divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP);
            report.setAverageBookingValue(average);
        } else {
            report.setAverageBookingValue(BigDecimal.ZERO);
        }
        
        // Số homestay có booking trong khoảng thời gian
        Long totalHomestays = bookingRepository.countDistinctHomestayIdByCreatedAtBetween(startDate, endDate);
        report.setTotalHomestays(totalHomestays);
        
        // Số khách hàng trong khoảng thời gian
        Long totalCustomers = bookingRepository.countDistinctUserIdByCreatedAtBetween(startDate, endDate);
        report.setTotalCustomers(totalCustomers);
        
        // Thời gian tạo báo cáo
        report.setReportGeneratedAt(LocalDateTime.now());
        report.setPeriod(startDate.toLocalDate() + " đến " + endDate.toLocalDate());
        
        return report;
    }
}
