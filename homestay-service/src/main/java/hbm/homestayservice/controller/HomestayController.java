package hbm.homestayservice.controller;

import hbm.homestayservice.dto.CreateHomestayRequest;
import hbm.homestayservice.dto.HomestayDTO;
import hbm.homestayservice.dto.HomestayPendingDTO;
import hbm.homestayservice.dto.UpdateHomestayRequest;
import hbm.homestayservice.dto.UpdateHomestayStatusRequest;
import hbm.homestayservice.service.HomestayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@CrossOrigin(origins = "http://localhost:3200")
public class HomestayController {
    
    @Autowired
    private HomestayService homestayService;
    
    /**
     * API lấy danh sách homestay công khai với các bộ lọc
     * GET http://localhost:8082/api/homestays
     * 
     * Query Parameters:
     * - city: Lọc theo thành phố (optional)
     * - capacity: Lọc theo sức chứa tối thiểu (optional)
     * - checkIn: Ngày nhận phòng (format: yyyy-MM-dd) (optional)
     * - checkOut: Ngày trả phòng (format: yyyy-MM-dd) (optional)
     */
    @GetMapping("/homestays")
    public ResponseEntity<Map<String, Object>> getPublicHomestays(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Short capacity,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut
    ) {
        try {
            log.info("GET /api/homestays - city={}, capacity={}, checkIn={}, checkOut={}", 
                     city, capacity, checkIn, checkOut);
            List<HomestayDTO> homestays = homestayService.getPublicHomestays(city, capacity, checkIn, checkOut);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Danh sách homestay công khai");
            response.put("data", homestays);
            response.put("total", homestays.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("GET /api/homestays - ERROR", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách homestay: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API tạo homestay mới
     * POST http://localhost:8082/api/homestays
     * 
     * Request Body: JSON object chứa thông tin homestay
     */
    @PostMapping("/homestays")
    public ResponseEntity<Map<String, Object>> createHomestay(@RequestBody CreateHomestayRequest request) {
        try {
            HomestayDTO createdHomestay = homestayService.createHomestay(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo homestay thành công. Homestay đang chờ admin duyệt.");
            response.put("data", createdHomestay);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi tạo homestay: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * API lấy danh sách homestay của chủ nhà hiện tại
     * GET http://localhost:8082/api/homestays/mine
     * 
     * Query Parameters:
     * - userId: ID của chủ nhà (bắt buộc)
     */
    @GetMapping("/homestays/mine")
    public ResponseEntity<Map<String, Object>> getMyHomestays(
            @RequestParam Long userId
    ) {
        try {
            List<HomestayDTO> homestays = homestayService.getMyHomestays(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Danh sách homestay của bạn");
            response.put("data", homestays);
            response.put("total", homestays.size());
            
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
    
    /**
     * API chuyển đổi trạng thái homestay
     * PATCH http://localhost:8082/api/homestays/{id}/status
     * 
     * Path Variable:
     * - id: ID của homestay
     * 
     * Query Parameters:
     * - userId: ID của chủ nhà (bắt buộc)
     * 
     * Request Body:
     * {
     *   "status": 2  // 2: công khai, 3: tạm ẩn, 4: bị khóa
     * }
     */
    @PatchMapping("/homestays/{id}/status")
    public ResponseEntity<Map<String, Object>> updateHomestayStatus(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody UpdateHomestayStatusRequest request
    ) {
        try {
            HomestayDTO updatedHomestay = homestayService.updateHomestayStatus(id, userId, request);
            
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
     * API host gửi yêu cầu cập nhật thông tin homestay
     * POST http://localhost:8082/api/homestays/{id}/update-request?userId={userId}
     * 
     * Body (JSON):
     * {
     *   "name": "Homestay ABC Updated",
     *   "description": "Mô tả mới",
     *   "address": "Địa chỉ mới",
     *   "city": "Đà Nẵng",
     *   "lat": 16.0544,
     *   "longitude": 108.2022,
     *   "capacity": 6,
     *   "numRooms": 3,
     *   "bathroomCount": 2,
     *   "basePrice": 800000,
     *   "amenities": "{\"wifi\": true, \"parking\": true}"
     * }
     */
    @PostMapping("/homestays/{id}/update-request")
    public ResponseEntity<Map<String, Object>> requestUpdateHomestay(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody UpdateHomestayRequest request
    ) {
        try {
            HomestayPendingDTO pending = homestayService.requestUpdateHomestay(id, userId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Yêu cầu cập nhật homestay đã được gửi, đang chờ admin duyệt");
            response.put("data", pending);
            
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
            errorResponse.put("message", "Lỗi khi tạo yêu cầu cập nhật: " + e.getMessage());
            errorResponse.put("data", null);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/homestays/{id}")
    public ResponseEntity<Map<String, Object>> getHomestayById(@PathVariable Long id) {
        try {
            HomestayDTO homestay = homestayService.getHomestayById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy homestay thành công");
            response.put("data", homestay);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * API lấy danh sách homestay của host (cho Dashboard)
     * GET http://localhost:8081/api/v1/homestays/my-homestays
     * 
     * Header:
     * - X-User-Id: ID của host (bắt buộc)
     */
    @GetMapping("/v1/homestays/my-homestays")
    public ResponseEntity<List<HomestayDTO>> getMyHomestaysForDashboard(
            @RequestHeader("X-User-Id") Long userId
    ) {
        try {
            log.info("GET /api/v1/homestays/my-homestays - userId: {}", userId);
            List<HomestayDTO> homestays = homestayService.getHomestaysByOwnerId(userId);
            return ResponseEntity.ok(homestays);
        } catch (IllegalArgumentException e) {
            log.error("Error getting homestays for owner: {}", userId, e);
            throw e;
        } catch (Exception e) {
            log.error("Error getting homestays for owner: {}", userId, e);
            throw e;
        }
    }
}
