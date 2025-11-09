package hbm.adminservice.controller;

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
}
