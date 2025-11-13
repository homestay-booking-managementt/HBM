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
     * API lấy toàn bộ danh sách homestay (bao gồm cả homestay bị ẩn, khóa)
     * GET http://localhost:8083/api/admin/homestays
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Lấy danh sách homestay thành công",
     *   "data": [...]
     * }
     */
    @GetMapping("/homestays")
    public ResponseEntity<Map<String, Object>> getAllHomestays() {
        try {
            java.util.List<HomestayDTO> homestays = adminHomestayService.getAllHomestaysForAdmin();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách homestay thành công");
            response.put("data", homestays);
            response.put("total", homestays.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách homestay: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API lấy danh sách homestay có yêu cầu cập nhật đang chờ duyệt
     * GET http://localhost:8083/api/admin/homestays-pending-update
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Lấy danh sách homestay có yêu cầu cập nhật thành công",
     *   "data": [{
     *     "homestay": {...},
     *     "pendingId": 1,
     *     "pendingData": "{...}",
     *     "submittedAt": "2025-11-11T10:00:00",
     *     "status": "waiting"
     *   }]
     * }
     */
    @GetMapping("/homestays-pending-update")
    public ResponseEntity<Map<String, Object>> getHomestaysPendingUpdate() {
        try {
            java.util.List<Map<String, Object>> pendingRequests = adminHomestayService.getHomestaysPendingUpdate();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách homestay có yêu cầu cập nhật thành công");
            response.put("data", pendingRequests);
            response.put("total", pendingRequests.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách homestay: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
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
    
    /**
     * API cập nhật trạng thái homestay (đơn giản hóa)
     * PUT http://localhost:8083/api/admin/homestays/{id}/status
     * 
     * Request Body:
     * {
     *   "status": 1  // 0: Inactive, 1: Active, 2: Pending, 3: Banned
     * }
     */
    @PutMapping("/homestays/{id}/status")
    public ResponseEntity<Map<String, Object>> updateHomestayStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request
    ) {
        try {
            Object statusObj = request.get("status");
            if (statusObj == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Trạng thái không được để trống");
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Integer status = Integer.valueOf(statusObj.toString());
            String reason = request.get("reason") != null ? request.get("reason").toString() : null;
            
            HomestayDTO updatedHomestay = adminHomestayService.updateHomestayStatus(id, status, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái homestay thành công");
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
    
    /**
     * API lấy chi tiết homestay
     * GET http://localhost:8083/api/admin/homestays/{id}
     */
    @GetMapping("/homestays/{id}")
    public ResponseEntity<Map<String, Object>> getHomestayDetail(@PathVariable Long id) {
        try {
            HomestayDTO homestay = adminHomestayService.getHomestayDetail(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin homestay thành công");
            response.put("data", homestay);
            
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
            errorResponse.put("message", "Lỗi khi lấy thông tin homestay: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API lấy lịch sử thay đổi trạng thái của homestay
     * GET http://localhost:8083/api/admin/homestays/{id}/status-history
     */
    @GetMapping("/homestays/{id}/status-history")
    public ResponseEntity<Map<String, Object>> getHomestayStatusHistory(@PathVariable Long id) {
        try {
            java.util.List<Map<String, Object>> history = adminHomestayService.getHomestayStatusHistory(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử trạng thái homestay thành công");
            response.put("data", history);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy lịch sử trạng thái: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API duyệt yêu cầu cập nhật homestay
     * POST http://localhost:8083/api/admin/homestay-pending/{id}/approve
     * 
     * Request Body:
     * {
     *   "adminId": 1
     * }
     */
    @PostMapping("/homestay-pending/{id}/approve")
    public ResponseEntity<Map<String, Object>> approvePendingUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long adminId = Long.valueOf(request.get("adminId").toString());
            adminHomestayService.approvePendingUpdate(id, adminId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã duyệt yêu cầu cập nhật homestay thành công");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi duyệt yêu cầu: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API từ chối yêu cầu cập nhật homestay
     * POST http://localhost:8083/api/admin/homestay-pending/{id}/reject
     * 
     * Request Body:
     * {
     *   "adminId": 1,
     *   "reason": "Thông tin không đầy đủ"
     * }
     */
    @PostMapping("/homestay-pending/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectPendingUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long adminId = Long.valueOf(request.get("adminId").toString());
            String reason = request.get("reason") != null ? request.get("reason").toString() : null;
            
            adminHomestayService.rejectPendingUpdate(id, adminId, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã từ chối yêu cầu cập nhật homestay");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi từ chối yêu cầu: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API lấy danh sách homestay theo owner ID
     * GET http://localhost:8083/api/admin/homestays/owner/{ownerId}
     * 
     * Response:
     * {
     *   "success": true,
     *   "message": "Lấy danh sách homestay của chủ nhà thành công",
     *   "data": [...],
     *   "total": 5,
     *   "ownerInfo": {
     *     "id": 123,
     *     "name": "Nguyễn Văn A",
     *     "email": "owner@example.com"
     *   }
     * }
     */
    @GetMapping("/homestays/owner/{ownerId}")
    public ResponseEntity<Map<String, Object>> getHomestaysByOwnerId(@PathVariable Long ownerId) {
        try {
            java.util.List<HomestayDTO> homestays = adminHomestayService.getHomestaysByOwnerId(ownerId);
            
            // Lấy thông tin chủ nhà từ homestay đầu tiên (nếu có)
            Map<String, Object> ownerInfo = null;
            if (!homestays.isEmpty() && homestays.get(0).getHost() != null) {
                ownerInfo = new HashMap<>();
                ownerInfo.put("id", homestays.get(0).getHost().getId());
                ownerInfo.put("name", homestays.get(0).getHost().getName());
                ownerInfo.put("email", homestays.get(0).getHost().getEmail());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách homestay của chủ nhà thành công");
            response.put("data", homestays);
            response.put("total", homestays.size());
            response.put("ownerInfo", ownerInfo);
            
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
            errorResponse.put("message", "Lỗi khi lấy danh sách homestay: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
