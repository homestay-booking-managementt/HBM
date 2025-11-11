package hbm.adminservice.controller;

import hbm.adminservice.dto.AssignComplaintRequest;
import hbm.adminservice.dto.ComplaintDTO;
import hbm.adminservice.dto.RespondComplaintRequest;
import hbm.adminservice.service.AdminComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminComplaintController {
    
    @Autowired
    private AdminComplaintService adminComplaintService;
    
    /**
     * API lấy danh sách khiếu nại
     * GET http://localhost:8083/api/admin/complaints
     * 
     * Query Parameters (tất cả đều optional):
     * - status: Lọc theo trạng thái (pending, in_progress, resolved, closed)
     * - userId: Lọc theo người khiếu nại
     * - adminId: Lọc theo admin được giao
     * - unassigned: true để lấy khiếu nại chưa được giao
     */
    @GetMapping("/complaints")
    public ResponseEntity<Map<String, Object>> getComplaints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Boolean unassigned
    ) {
        try {
            List<ComplaintDTO> complaints = adminComplaintService.getComplaints(status, userId, adminId, unassigned);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Danh sách khiếu nại");
            response.put("data", complaints);
            response.put("total", complaints.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách khiếu nại: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API lấy chi tiết khiếu nại
     * GET http://localhost:8083/api/admin/complaints/{id}
     */
    @GetMapping("/complaints/{id}")
    public ResponseEntity<Map<String, Object>> getComplaintById(@PathVariable Long id) {
        try {
            ComplaintDTO complaint = adminComplaintService.getComplaintById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Chi tiết khiếu nại");
            response.put("data", complaint);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy chi tiết khiếu nại: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API giao khiếu nại cho admin
     * POST http://localhost:8083/api/admin/complaints/{id}/assign
     * 
     * Request Body:
     * {
     *   "adminId": 10
     * }
     */
    @PostMapping("/complaints/{id}/assign")
    public ResponseEntity<Map<String, Object>> assignComplaint(
            @PathVariable Long id,
            @RequestBody AssignComplaintRequest request
    ) {
        try {
            ComplaintDTO complaint = adminComplaintService.assignComplaint(id, request.getAdminId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Giao khiếu nại cho admin thành công");
            response.put("data", complaint);
            
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
            errorResponse.put("message", "Lỗi khi giao khiếu nại: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API admin phản hồi khiếu nại
     * POST http://localhost:8083/api/admin/complaints/{id}/respond
     * 
     * Query Parameters:
     * - adminId: ID của admin đang phản hồi
     * 
     * Request Body:
     * {
     *   "status": "resolved",
     *   "adminResponse": "Chúng tôi đã xử lý vấn đề của bạn..."
     * }
     */
    @PostMapping("/complaints/{id}/respond")
    public ResponseEntity<Map<String, Object>> respondToComplaint(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestBody RespondComplaintRequest request
    ) {
        try {
            ComplaintDTO complaint = adminComplaintService.respondToComplaint(
                    id, 
                    adminId, 
                    request.getStatus(), 
                    request.getAdminResponse()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Phản hồi khiếu nại thành công");
            response.put("data", complaint);
            
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
            errorResponse.put("message", "Lỗi khi phản hồi khiếu nại: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API cập nhật status khiếu nại
     * PATCH http://localhost:8083/api/admin/complaints/{id}/status
     * 
     * Request Body:
     * {
     *   "status": "closed"
     * }
     */
    @PatchMapping("/complaints/{id}/status")
    public ResponseEntity<Map<String, Object>> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        try {
            String newStatus = request.get("status");
            
            if (newStatus == null || newStatus.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Status không được để trống");
                errorResponse.put("data", null);
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            ComplaintDTO complaint = adminComplaintService.updateComplaintStatus(id, newStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái khiếu nại thành công");
            response.put("data", complaint);
            
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
            errorResponse.put("message", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
