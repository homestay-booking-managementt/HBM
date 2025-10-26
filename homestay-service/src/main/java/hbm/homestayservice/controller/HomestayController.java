package hbm.homestayservice.controller;

import hbm.homestayservice.dto.CreateHomestayRequest;
import hbm.homestayservice.dto.HomestayDTO;
import hbm.homestayservice.service.HomestayService;
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
            List<HomestayDTO> homestays = homestayService.getPublicHomestays(city, capacity, checkIn, checkOut);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Danh sách homestay công khai");
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
}
