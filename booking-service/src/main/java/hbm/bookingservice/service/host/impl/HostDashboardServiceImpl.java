package hbm.bookingservice.service.host.impl;

import hbm.bookingservice.dto.host.*;
import hbm.bookingservice.repository.BookingRepository;
import hbm.bookingservice.repository.HomestayRepository;
import hbm.bookingservice.repository.PaymentRepository;
import hbm.bookingservice.service.host.HostDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HostDashboardServiceImpl implements HostDashboardService {
    
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final HomestayRepository homestayRepository;
    
    @Override
    public HostStatisticsDto getHostStatistics(Long hostId) {
        log.info("Fetching statistics for host: {}", hostId);
        
        try {
            // Homestay statistics
            Long totalHomestays = (long) homestayRepository.findHomestayIdsByUserId(hostId).size();
            Long approvedHomestays = homestayRepository.countByUserIdAndStatus(hostId, (byte) 1);
            Long pendingHomestays = totalHomestays - approvedHomestays;
            
            // Booking statistics
            List<BookingStatusCount> bookingCounts = bookingRepository.countBookingsByStatusForHost(hostId);
            Map<String, Long> bookingMap = new HashMap<>();
            for (BookingStatusCount count : bookingCounts) {
                bookingMap.put(count.getStatus(), count.getCount());
            }
            
            Long totalBookings = bookingMap.values().stream().mapToLong(Long::longValue).sum();
            Long pendingBookings = bookingMap.getOrDefault("pending_payment", 0L);
            Long confirmedBookings = bookingMap.getOrDefault("confirmed", 0L);
            Long completedBookings = bookingMap.getOrDefault("completed", 0L);
            Long cancelledBookings = bookingMap.getOrDefault("cancelled", 0L);
            
            // Payment statistics
            List<PaymentStatusCount> paymentCounts = paymentRepository.countPaymentsByStatusForHost(hostId);
            Map<String, Long> paymentMap = new HashMap<>();
            for (PaymentStatusCount count : paymentCounts) {
                paymentMap.put(count.getStatus(), count.getCount());
            }
            
            Long totalPayments = paymentMap.values().stream().mapToLong(Long::longValue).sum();
            Long completedPayments = paymentMap.getOrDefault("success", 0L); // Changed from "completed" to "success"
            Long pendingPayments = paymentMap.getOrDefault("pending", 0L);
            Long failedPayments = paymentMap.getOrDefault("failed", 0L);
            
            HostStatisticsDto statistics = HostStatisticsDto.builder()
                    .totalHomestays(totalHomestays)
                    .approvedHomestays(approvedHomestays)
                    .pendingHomestays(pendingHomestays)
                    .totalBookings(totalBookings)
                    .pendingBookings(pendingBookings)
                    .confirmedBookings(confirmedBookings)
                    .completedBookings(completedBookings)
                    .cancelledBookings(cancelledBookings)
                    .totalPayments(totalPayments)
                    .completedPayments(completedPayments)
                    .pendingPayments(pendingPayments)
                    .failedPayments(failedPayments)
                    .build();
            
            log.info("Successfully fetched statistics for host: {}", hostId);
            return statistics;
            
        } catch (Exception e) {
            log.error("Error fetching statistics for host: {}", hostId, e);
            throw e;
        }
    }
    
    @Override
    public RevenueStatisticsDto getRevenueStatistics(Long hostId, String period) {
        log.info("Fetching revenue statistics for host: {} with period: {}", hostId, period);
        
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate;
            List<PeriodRevenueProjection> periodRevenues;
            
            switch (period.toLowerCase()) {
                case "week":
                    // Last 7 days
                    startDate = endDate.minusDays(6);
                    periodRevenues = bookingRepository.getWeeklyRevenue(hostId, startDate, endDate);
                    break;
                    
                case "month":
                    // Current month
                    startDate = endDate.with(TemporalAdjusters.firstDayOfMonth());
                    periodRevenues = bookingRepository.getWeeklyRevenue(hostId, startDate, endDate);
                    break;
                    
                case "year":
                    // Current year
                    startDate = endDate.with(TemporalAdjusters.firstDayOfYear());
                    periodRevenues = bookingRepository.getMonthlyRevenue(hostId, startDate, endDate);
                    break;
                    
                default:
                    startDate = endDate.with(TemporalAdjusters.firstDayOfMonth());
                    periodRevenues = bookingRepository.getWeeklyRevenue(hostId, startDate, endDate);
            }
            
            // Calculate total revenue
            RevenueProjection totalRevenue = bookingRepository.calculateRevenueForPeriod(hostId, startDate, endDate);
            
            BigDecimal revenue = totalRevenue.getTotalRevenue() != null ? totalRevenue.getTotalRevenue() : BigDecimal.ZERO;
            Long bookings = totalRevenue.getTotalBookings() != null ? totalRevenue.getTotalBookings() : 0L;
            BigDecimal avgBookingValue = bookings > 0 
                    ? revenue.divide(BigDecimal.valueOf(bookings), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            
            // Build period data
            List<RevenueStatisticsDto.PeriodDataDto> periodData = new ArrayList<>();
            for (PeriodRevenueProjection proj : periodRevenues) {
                String periodLabel;
                if ("year".equals(period.toLowerCase())) {
                    periodLabel = "Tháng " + proj.getMonthNumber();
                } else {
                    periodLabel = "Tuần " + proj.getWeekNumber();
                }
                
                periodData.add(RevenueStatisticsDto.PeriodDataDto.builder()
                        .period(periodLabel)
                        .revenue(proj.getRevenue() != null ? proj.getRevenue() : BigDecimal.ZERO)
                        .bookings(proj.getBookings() != null ? proj.getBookings() : 0L)
                        .build());
            }
            
            RevenueStatisticsDto statistics = RevenueStatisticsDto.builder()
                    .totalRevenue(revenue)
                    .totalBookings(bookings)
                    .averageBookingValue(avgBookingValue)
                    .period(period)
                    .periodData(periodData)
                    .build();
            
            log.info("Successfully fetched revenue statistics for host: {}", hostId);
            return statistics;
            
        } catch (Exception e) {
            log.error("Error fetching revenue statistics for host: {}", hostId, e);
            throw e;
        }
    }
    
    @Override
    public List<TopHomestayDto> getTopHomestays(Long hostId, int limit) {
        log.info("Fetching top {} homestays for host: {}", limit, hostId);
        
        try {
            List<HomestayRevenueProjection> topHomestays = bookingRepository.getTopHomestaysByRevenue(
                    hostId, 
                    PageRequest.of(0, limit)
            );
            
            List<TopHomestayDto> result = new ArrayList<>();
            for (HomestayRevenueProjection proj : topHomestays) {
                result.add(TopHomestayDto.builder()
                        .homestayId(proj.getHomestayId())
                        .homestayName(proj.getHomestayName())
                        .totalRevenue(proj.getTotalRevenue() != null ? proj.getTotalRevenue() : BigDecimal.ZERO)
                        .totalBookings(proj.getTotalBookings() != null ? proj.getTotalBookings() : 0L)
                        .build());
            }
            
            log.info("Successfully fetched {} top homestays for host: {}", result.size(), hostId);
            return result;
            
        } catch (Exception e) {
            log.error("Error fetching top homestays for host: {}", hostId, e);
            throw e;
        }
    }
}
