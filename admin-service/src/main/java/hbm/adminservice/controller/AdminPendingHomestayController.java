package hbm.adminservice.controller;

import hbm.adminservice.dto.HomestayDTO;
import hbm.adminservice.service.AdminPendingHomestayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminPendingHomestayController {
    
    @Autowired
    private AdminPendingHomestayService adminPendingHomestayService;
    
    /**
     * API lấy danh sách homestay chờ duyệt
     * GET http://localhost:8083/api/admin/homestays/pending
     * 
     * Lấy tất cả homestay có status = 1 (chờ duyệt) khi host tạo mới
     * Sắp xếp theo thời gian tạo mới nhất lên đầu
     * Bao gồm cả danh sách ảnh của homestay
     */
    @GetMapping("/homestays/pending")
    public ResponseEntity<Map<String, Object>> getPendingHomestays() {
        try {
            List<HomestayDTO> homestays = adminPendingHomestayService.getPendingHomestays();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Danh sách homestay chờ duyệt");
            response.put("data", homestays);
            response.put("total", homestays.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách homestay chờ duyệt: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
