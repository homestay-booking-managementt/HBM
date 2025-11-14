package hbm.bookingservice.controller;

import hbm.bookingservice.dto.host.HostStatisticsDto;
import hbm.bookingservice.dto.host.RevenueStatisticsDto;
import hbm.bookingservice.dto.host.TopHomestayDto;
import hbm.bookingservice.service.host.HostDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/host/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3200")
public class HostDashboardController {
    
    private final HostDashboardService hostDashboardService;
    
    /**
     * GET /api/v1/host/dashboard/statistics
     * Lấy thống kê tổng quan cho host
     */
    @GetMapping("/statistics")
    public ResponseEntity<HostStatisticsDto> getHostStatistics(
            @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("GET /api/v1/host/dashboard/statistics - userId: {}", userId);
        
        try {
            HostStatisticsDto statistics = hostDashboardService.getHostStatistics(userId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error getting host statistics for userId: {}", userId, e);
            throw e;
        }
    }
    
    /**
     * GET /api/v1/host/dashboard/revenue?period=month
     * Lấy thống kê doanh thu theo period
     */
    @GetMapping("/revenue")
    public ResponseEntity<RevenueStatisticsDto> getRevenueStatistics(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "month") String period
    ) {
        log.info("GET /api/v1/host/dashboard/revenue - userId: {}, period: {}", userId, period);
        
        try {
            // Validate period parameter
            if (!period.matches("week|month|year")) {
                throw new IllegalArgumentException("Period must be one of: week, month, year");
            }
            
            RevenueStatisticsDto statistics = hostDashboardService.getRevenueStatistics(userId, period);
            return ResponseEntity.ok(statistics);
        } catch (IllegalArgumentException e) {
            log.error("Invalid period parameter: {}", period, e);
            throw e;
        } catch (Exception e) {
            log.error("Error getting revenue statistics for userId: {}", userId, e);
            throw e;
        }
    }
    
    /**
     * GET /api/v1/host/dashboard/top-homestays?limit=5
     * Lấy top homestays theo doanh thu
     */
    @GetMapping("/top-homestays")
    public ResponseEntity<List<TopHomestayDto>> getTopHomestays(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        log.info("GET /api/v1/host/dashboard/top-homestays - userId: {}, limit: {}", userId, limit);
        
        try {
            // Validate limit parameter
            if (limit < 1 || limit > 100) {
                throw new IllegalArgumentException("Limit must be between 1 and 100");
            }
            
            List<TopHomestayDto> topHomestays = hostDashboardService.getTopHomestays(userId, limit);
            return ResponseEntity.ok(topHomestays);
        } catch (IllegalArgumentException e) {
            log.error("Invalid limit parameter: {}", limit, e);
            throw e;
        } catch (Exception e) {
            log.error("Error getting top homestays for userId: {}", userId, e);
            throw e;
        }
    }
}
