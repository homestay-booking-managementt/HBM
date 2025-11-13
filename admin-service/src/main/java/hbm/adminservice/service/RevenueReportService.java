package hbm.adminservice.service;

import hbm.adminservice.dto.RevenueReportDTO;
import hbm.adminservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    /**
     * Lấy xu hướng doanh thu theo ngày
     */
    public List<hbm.adminservice.dto.RevenueTrendDTO> getRevenueTrends(LocalDateTime startDate, LocalDateTime endDate) {
        List<hbm.adminservice.entity.Booking> bookings = bookingRepository.findBookingsForTrends(startDate, endDate);
        
        // Nhóm bookings theo ngày
        Map<String, List<hbm.adminservice.entity.Booking>> bookingsByDate = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getCreatedAt().toLocalDate().toString()));
        
        // Tạo danh sách RevenueTrendDTO
        return bookingsByDate.entrySet().stream()
                .map(entry -> {
                    String date = entry.getKey();
                    List<hbm.adminservice.entity.Booking> dayBookings = entry.getValue();
                    
                    BigDecimal revenue = dayBookings.stream()
                            .map(hbm.adminservice.entity.Booking::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return new hbm.adminservice.dto.RevenueTrendDTO(date, revenue, (long) dayBookings.size());
                })
                .sorted(Comparator.comparing(hbm.adminservice.dto.RevenueTrendDTO::getDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy doanh thu theo trạng thái
     */
    public List<hbm.adminservice.dto.RevenueByStatusDTO> getRevenueByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        List<hbm.adminservice.dto.RevenueByStatusDTO> result = new java.util.ArrayList<>();
        
        // Danh sách các trạng thái cần thống kê
        String[] statuses = {"completed", "confirmed", "pending"};
        
        for (String status : statuses) {
            BigDecimal revenue = bookingRepository.sumTotalPriceByStatusAndCreatedAtBetween(status, startDate, endDate);
            Long count = bookingRepository.countByStatusAndCreatedAtBetween(status, startDate, endDate);
            
            result.add(new hbm.adminservice.dto.RevenueByStatusDTO(
                    status,
                    revenue != null ? revenue : BigDecimal.ZERO,
                    count != null ? count : 0L
            ));
        }
        
        // Sắp xếp theo revenue giảm dần
        result.sort((a, b) -> b.getRevenue().compareTo(a.getRevenue()));
        
        return result;
    }
    
    /**
     * Lấy top homestays theo doanh thu
     */
    public List<hbm.adminservice.dto.TopHomestayDTO> getTopHomestays(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        List<Object[]> results = bookingRepository.findTopHomestaysByRevenue(startDate, endDate, limit);
        
        // Tính tổng doanh thu để tính phần trăm
        BigDecimal totalRevenue = bookingRepository.sumTotalPriceByStatusNotInAndCreatedAtBetween("cancelled", startDate, endDate);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        List<hbm.adminservice.dto.TopHomestayDTO> topHomestays = new java.util.ArrayList<>();
        
        for (Object[] row : results) {
            Long homestayId = ((Number) row[0]).longValue();
            String homestayName = (String) row[1];
            Long bookingCount = ((Number) row[2]).longValue();
            BigDecimal revenue = (BigDecimal) row[3];
            
            // Tính phần trăm
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                percentage = revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            
            topHomestays.add(new hbm.adminservice.dto.TopHomestayDTO(
                    homestayId,
                    homestayName,
                    bookingCount,
                    revenue,
                    percentage
            ));
        }
        
        return topHomestays;
    }
    
    /**
     * Lấy doanh thu theo tháng
     */
    public List<hbm.adminservice.dto.MonthlyRevenueDTO> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<hbm.adminservice.entity.Booking> bookings = bookingRepository.findBookingsForMonthly(startDate, endDate);
        
        // Nhóm bookings theo tháng (YYYY-MM)
        Map<String, List<hbm.adminservice.entity.Booking>> bookingsByMonth = bookings.stream()
                .collect(Collectors.groupingBy(b -> 
                    b.getCreatedAt().getYear() + "-" + 
                    String.format("%02d", b.getCreatedAt().getMonthValue())
                ));
        
        // Tạo danh sách MonthlyRevenueDTO
        List<hbm.adminservice.dto.MonthlyRevenueDTO> result = bookingsByMonth.entrySet().stream()
                .map(entry -> {
                    String month = entry.getKey();
                    List<hbm.adminservice.entity.Booking> monthBookings = entry.getValue();
                    
                    BigDecimal revenue = monthBookings.stream()
                            .map(hbm.adminservice.entity.Booking::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return new hbm.adminservice.dto.MonthlyRevenueDTO(month, revenue, (long) monthBookings.size());
                })
                .sorted(Comparator.comparing(hbm.adminservice.dto.MonthlyRevenueDTO::getMonth))
                .collect(Collectors.toList());
        
        // Giới hạn 12 tháng gần nhất
        if (result.size() > 12) {
            result = result.subList(result.size() - 12, result.size());
        }
        
        return result;
    }
    
    /**
     * Lấy dữ liệu so sánh với kỳ trước
     */
    public hbm.adminservice.dto.ComparisonDTO getComparisonData(LocalDateTime startDate, LocalDateTime endDate) {
        // Tính khoảng thời gian
        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();
        
        // Tính kỳ trước
        LocalDateTime previousStartDate = startDate.minusDays(daysBetween);
        LocalDateTime previousEndDate = startDate;
        
        // Lấy dữ liệu kỳ hiện tại
        BigDecimal currentRevenue = bookingRepository.sumTotalPriceByStatusNotInAndCreatedAtBetween("cancelled", startDate, endDate);
        Long currentBookings = bookingRepository.countByCreatedAtBetween(startDate, endDate);
        
        // Lấy dữ liệu kỳ trước
        BigDecimal previousRevenue = bookingRepository.sumTotalPriceByStatusNotInAndCreatedAtBetween("cancelled", previousStartDate, previousEndDate);
        Long previousBookings = bookingRepository.countByCreatedAtBetween(previousStartDate, previousEndDate);
        
        // Xử lý null values
        currentRevenue = currentRevenue != null ? currentRevenue : BigDecimal.ZERO;
        previousRevenue = previousRevenue != null ? previousRevenue : BigDecimal.ZERO;
        currentBookings = currentBookings != null ? currentBookings : 0L;
        previousBookings = previousBookings != null ? previousBookings : 0L;
        
        // Tính toán thay đổi
        BigDecimal revenueChange = currentRevenue.subtract(previousRevenue);
        Long bookingsChange = currentBookings - previousBookings;
        
        // Tính phần trăm thay đổi
        Double revenueChangePercentage = 0.0;
        if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revenueChangePercentage = revenueChange.divide(previousRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        
        Double bookingsChangePercentage = 0.0;
        if (previousBookings > 0) {
            bookingsChangePercentage = ((double) bookingsChange / previousBookings) * 100;
        }
        
        return new hbm.adminservice.dto.ComparisonDTO(
                currentRevenue,
                previousRevenue,
                revenueChange,
                revenueChangePercentage,
                currentBookings,
                previousBookings,
                bookingsChange,
                bookingsChangePercentage
        );
    }
}
