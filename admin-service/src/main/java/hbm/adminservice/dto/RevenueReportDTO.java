package hbm.adminservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDTO {
    
    // Tổng quan
    private BigDecimal totalRevenue;           // Tổng doanh thu
    private Long totalBookings;                 // Tổng số đơn
    private Long completedBookings;             // Số đơn hoàn thành
    private Long cancelledBookings;             // Số đơn bị hủy
    private Long pendingBookings;               // Số đơn đang chờ
    
    // Doanh thu theo trạng thái
    private BigDecimal completedRevenue;        // Doanh thu từ đơn hoàn thành
    private BigDecimal pendingRevenue;          // Doanh thu từ đơn đang chờ
    private BigDecimal confirmedRevenue;        // Doanh thu từ đơn đã xác nhận
    
    // Thống kê
    private BigDecimal averageBookingValue;     // Giá trị trung bình mỗi đơn
    private Long totalHomestays;                // Số homestay có booking
    private Long totalCustomers;                // Số khách hàng
    
    // Thời gian báo cáo
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportGeneratedAt;
    
    private String period;                      // Khoảng thời gian báo cáo (optional)
}
