package hbm.adminservice.controller;

import hbm.adminservice.dto.BookingDTO;
import hbm.adminservice.dto.CustomerBookingsResponse;
import hbm.adminservice.service.AdminBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminBookingController {
    
    @Autowired
    private AdminBookingService adminBookingService;
    
    /**
     * API lấy danh sách booking
     * GET http://localhost:8083/api/admin/bookings
     * 
     * Query Parameters (tất cả đều optional):
     * - status: Lọc theo trạng thái (pending, confirmed, cancelled, completed)
     * - userId: Lọc theo ID khách hàng
     * - homestayId: Lọc theo ID homestay
     * 
     * Ví dụ:
     * - GET /api/admin/bookings                        -> Tất cả booking
     * - GET /api/admin/bookings?status=pending         -> Booking đang chờ
     * - GET /api/admin/bookings?userId=5               -> Booking của user 5
     * - GET /api/admin/bookings?homestayId=3           -> Booking của homestay 3
     */
    @GetMapping("/bookings")
    public ResponseEntity<Map<String, Object>> getAllBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long homestayId
    ) {
        try {
            List<BookingDTO> bookings = adminBookingService.getAllBookings(status, userId, homestayId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Danh sách đơn đặt phòng");
            response.put("data", bookings);
            response.put("total", bookings.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách booking: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API lấy danh sách booking của một customer cụ thể
     * GET http://localhost:8083/api/admin/bookings/customer/{customerId}
     * 
     * Response:
     * {
     *   "bookings": [...],
     *   "customerInfo": {
     *     "id": 123,
     *     "name": "Nguyễn Văn An",
     *     "email": "an.nguyen@example.com",
     *     "phone": "0901111222"
     *   }
     * }
     */
    @GetMapping("/bookings/customer/{customerId}")
    public ResponseEntity<CustomerBookingsResponse> getBookingsByCustomerId(@PathVariable Long customerId) {
        try {
            CustomerBookingsResponse response = adminBookingService.getBookingsByCustomerId(customerId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Customer not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * API lấy chi tiết booking theo ID
     * GET http://localhost:8083/api/admin/bookings/{id}
     */
    @GetMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable Long id) {
        try {
            BookingDTO booking = adminBookingService.getBookingById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Chi tiết đơn đặt phòng");
            response.put("data", booking);
            
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
            errorResponse.put("message", "Lỗi khi lấy chi tiết booking: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API cập nhật status của booking
     * PATCH http://localhost:8083/api/admin/bookings/{id}/status
     * 
     * Request Body:
     * {
     *   "status": "confirmed"
     * }
     */
    @PatchMapping("/bookings/{id}/status")
    public ResponseEntity<Map<String, Object>> updateBookingStatus(
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
            
            BookingDTO updatedBooking = adminBookingService.updateBookingStatus(id, newStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái booking thành công");
            response.put("data", updatedBooking);
            
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
            errorResponse.put("message", "Lỗi khi cập nhật status booking: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
