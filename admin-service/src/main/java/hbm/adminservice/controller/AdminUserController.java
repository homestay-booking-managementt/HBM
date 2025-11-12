package hbm.adminservice.controller;

import hbm.adminservice.dto.UpdateUserStatusRequest;
import hbm.adminservice.dto.UserDTO;
import hbm.adminservice.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    /**
     * API lấy danh sách người dùng
     * GET http://localhost:8083/api/admin/users
     * 
     * Query Parameters:
     * - role: Lọc theo vai trò (CUSTOMER, HOST, ADMIN) - optional
     * 
     * Ví dụ:
     * - GET /api/admin/users                  -> Lấy tất cả user
     * - GET /api/admin/users?role=CUSTOMER    -> Lấy user có role CUSTOMER
     * - GET /api/admin/users?role=HOST        -> Lấy user có role HOST
     * - GET /api/admin/users?role=ADMIN       -> Lấy user có role ADMIN
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUserList(
            @RequestParam(required = false) String role
    ) {
        try {
            List<UserDTO> users = adminUserService.getUsersByRole(role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách người dùng thành công");
            response.put("data", users);
            response.put("total", users.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách người dùng: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API cập nhật trạng thái user
     * PUT http://localhost:8083/api/admin/users/{userId}/status
     * 
     * Request Body:
     * {
     *   "status": 1,  // 0:chờ duyệt, 1:hoạt động, 2:tạm khóa, 3:bị chặn
     *   "reason": "Lý do thay đổi trạng thái"  // optional
     * }
     * 
     * Ví dụ:
     * - PUT /api/admin/users/5/status
     *   Body: {"status": 1}  -> Kích hoạt user ID 5
     * 
     * - PUT /api/admin/users/5/status
     *   Body: {"status": 2, "reason": "Vi phạm chính sách"}  -> Tạm khóa user ID 5
     * 
     * - PUT /api/admin/users/5/status
     *   Body: {"status": 3, "reason": "Spam nhiều lần"}  -> Chặn user ID 5
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UpdateUserStatusRequest request
    ) {
        try {
            // TODO: Lấy admin ID từ JWT token (hiện tại hard-code)
            Long adminId = 1L;
            
            UserDTO updatedUser = adminUserService.updateUserStatus(
                    userId, 
                    request.getStatus(), 
                    request.getReason(), 
                    adminId
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái user thành công");
            response.put("data", updatedUser);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Lấy lịch sử thay đổi trạng thái của user
     * GET /api/admin/users/{userId}/status-history
     */
    @GetMapping("/users/{userId}/status-history")
    public ResponseEntity<Map<String, Object>> getUserStatusHistory(@PathVariable Long userId) {
        try {
            List<hbm.adminservice.dto.UserStatusHistoryDTO> history = adminUserService.getUserStatusHistory(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử trạng thái thành công");
            response.put("data", history);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy lịch sử: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
