package hbm.adminservice.controller;

import hbm.adminservice.dto.AdminUpdateStatusRequest;
import hbm.adminservice.dto.HomestayDTO;
import hbm.adminservice.service.AdminHomestayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminHomestayController {
    
    @Autowired
    private AdminHomestayService adminHomestayService;
    
    /**
     * API cho Admin duyệt/khóa homestay
     * PATCH http://localhost:8083/api/admin/homestays/{id}/status
     * 
     * Path Variable:
     * - id: ID của homestay
     * 
     * Query Parameters:
     * - adminId: ID của admin (bắt buộc)
     * 
     * Request Body:
     * {
     *   "status": 2,  // 2: duyệt & công khai, 3: tạm ẩn, 4: bị khóa
     *   "reason": "Vi phạm chính sách" // optional, lý do khóa/ẩn
     * }
     */
    @PatchMapping("/homestays/{id}/status")
    public ResponseEntity<Map<String, Object>> adminUpdateHomestayStatus(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody AdminUpdateStatusRequest request
    ) {
        try {
            HomestayDTO updatedHomestay = adminHomestayService.adminUpdateStatus(id, adminId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            String message;
            if (request.getStatus() == 2) {
                message = "Duyệt và công khai homestay thành công";
            } else if (request.getStatus() == 3) {
                message = "Tạm ẩn homestay thành công";
            } else {
                message = "Khóa homestay thành công";
            }
            
            if (request.getReason() != null && !request.getReason().isEmpty()) {
                message += ". Lý do: " + request.getReason();
            }
            
            response.put("message", message);
            response.put("data", updatedHomestay);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi cập nhật trạng thái homestay: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
