package hbm.adminservice.controller;

import hbm.adminservice.dto.HomestayPendingDTO;
import hbm.adminservice.dto.ReviewPendingRequest;
import hbm.adminservice.service.AdminPendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminPendingController {
    
    @Autowired
    private AdminPendingService adminPendingService;
    
    /**
     * API lấy danh sách yêu cầu cập nhật homestay đang chờ duyệt
     * GET http://localhost:8083/api/admin/homestay-pending
     */
    @GetMapping("/homestay-pending")
    public ResponseEntity<Map<String, Object>> getWaitingRequests() {
        try {
            List<HomestayPendingDTO> pendings = adminPendingService.getWaitingRequests();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Danh sách yêu cầu chờ duyệt");
            response.put("data", pendings);
            response.put("total", pendings.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API admin duyệt hoặc từ chối yêu cầu cập nhật homestay
     * POST http://localhost:8083/api/admin/homestay-pending/{id}/review?adminId={adminId}
     * 
     * Body (JSON):
     * {
     *   "action": "approve",  // hoặc "reject"
     *   "reason": "Lý do từ chối"  // bắt buộc nếu action = "reject"
     * }
     */
    @PostMapping("/homestay-pending/{id}/review")
    public ResponseEntity<Map<String, Object>> reviewPendingRequest(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody ReviewPendingRequest request
    ) {
        try {
            HomestayPendingDTO result;
            String message;
            
            if ("approve".equals(request.getAction())) {
                result = adminPendingService.approveRequest(id, adminId);
                message = "Đã duyệt yêu cầu cập nhật homestay thành công";
            } else if ("reject".equals(request.getAction())) {
                result = adminPendingService.rejectRequest(id, adminId, request.getReason());
                message = "Đã từ chối yêu cầu cập nhật homestay";
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Action không hợp lệ. Chỉ chấp nhận 'approve' hoặc 'reject'");
                errorResponse.put("data", null);
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);
            response.put("data", result);
            
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
            errorResponse.put("message", "Lỗi khi xử lý yêu cầu: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
