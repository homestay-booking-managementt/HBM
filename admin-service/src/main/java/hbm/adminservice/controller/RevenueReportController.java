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
}
