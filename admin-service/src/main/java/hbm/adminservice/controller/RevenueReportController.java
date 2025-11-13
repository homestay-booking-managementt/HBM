package hbm.adminservice.controller;

import hbm.adminservice.dto.RevenueReportDTO;
import hbm.adminservice.service.RevenueReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class RevenueReportController {
    
    private final RevenueReportService revenueReportService;
    
    /**
     * Lấy báo cáo doanh thu tổng hợp
     * GET /api/admin/reports/revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            RevenueReportDTO report;
            
            // Nếu có startDate và endDate, lấy báo cáo theo khoảng thời gian
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                report = revenueReportService.getRevenueReportByDateRange(startDateTime, endDateTime);
            } 
            // Nếu chỉ có startDate, lấy từ startDate đến hiện tại
            else if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = LocalDateTime.now();
                report = revenueReportService.getRevenueReportByDateRange(startDateTime, endDateTime);
            }
            // Nếu không có tham số, lấy tất cả
            else {
                report = revenueReportService.getRevenueReport();
            }
            
            response.put("success", true);
            response.put("message", "Báo cáo doanh thu");
            response.put("data", report);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy báo cáo doanh thu: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Lấy xu hướng doanh thu theo ngày
     * GET /api/admin/reports/revenue/trends
     */
    @GetMapping("/revenue/trends")
    public ResponseEntity<Map<String, Object>> getRevenueTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                response.put("success", false);
                response.put("message", "Ngày bắt đầu phải trước ngày kết thúc");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Default to last 30 days if not provided
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
            
            List<hbm.adminservice.dto.RevenueTrendDTO> trends = revenueReportService.getRevenueTrends(startDateTime, endDateTime);
            
            response.put("success", true);
            response.put("message", "Xu hướng doanh thu");
            response.put("data", trends);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy xu hướng doanh thu: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Lấy doanh thu theo trạng thái
     * GET /api/admin/reports/revenue/by-status
     */
    @GetMapping("/revenue/by-status")
    public ResponseEntity<Map<String, Object>> getRevenueByStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                response.put("success", false);
                response.put("message", "Ngày bắt đầu phải trước ngày kết thúc");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Default to last 30 days if not provided
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
            
            List<hbm.adminservice.dto.RevenueByStatusDTO> statusData = revenueReportService.getRevenueByStatus(startDateTime, endDateTime);
            
            response.put("success", true);
            response.put("message", "Doanh thu theo trạng thái");
            response.put("data", statusData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy doanh thu theo trạng thái: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Lấy top homestays theo doanh thu
     * GET /api/admin/reports/revenue/top-homestays
     */
    @GetMapping("/revenue/top-homestays")
    public ResponseEntity<Map<String, Object>> getTopHomestays(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                response.put("success", false);
                response.put("message", "Ngày bắt đầu phải trước ngày kết thúc");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate limit
            if (limit < 1 || limit > 100) {
                response.put("success", false);
                response.put("message", "Limit phải từ 1 đến 100");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Default to last 30 days if not provided
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
            
            List<hbm.adminservice.dto.TopHomestayDTO> topHomestays = revenueReportService.getTopHomestays(startDateTime, endDateTime, limit);
            
            response.put("success", true);
            response.put("message", "Top homestay theo doanh thu");
            response.put("data", topHomestays);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy top homestays: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Lấy doanh thu theo tháng
     * GET /api/admin/reports/revenue/monthly
     */
    @GetMapping("/revenue/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                response.put("success", false);
                response.put("message", "Ngày bắt đầu phải trước ngày kết thúc");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Default to last 12 months if not provided
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusMonths(12);
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
            
            List<hbm.adminservice.dto.MonthlyRevenueDTO> monthlyData = revenueReportService.getMonthlyRevenue(startDateTime, endDateTime);
            
            response.put("success", true);
            response.put("message", "Doanh thu theo tháng");
            response.put("data", monthlyData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy doanh thu theo tháng: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Lấy dữ liệu so sánh với kỳ trước
     * GET /api/admin/reports/revenue/comparison
     */
    @GetMapping("/revenue/comparison")
    public ResponseEntity<Map<String, Object>> getComparisonData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                response.put("success", false);
                response.put("message", "Ngày bắt đầu phải trước ngày kết thúc");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Default to last 30 days if not provided
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();
            
            hbm.adminservice.dto.ComparisonDTO comparisonData = revenueReportService.getComparisonData(startDateTime, endDateTime);
            
            response.put("success", true);
            response.put("message", "So sánh với kỳ trước");
            response.put("data", comparisonData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy dữ liệu so sánh: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
