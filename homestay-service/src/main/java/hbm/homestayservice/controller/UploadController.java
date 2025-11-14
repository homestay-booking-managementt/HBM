package hbm.homestayservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
@CrossOrigin(origins = "http://localhost:3200")
public class UploadController {
    
    // Thư mục lưu ảnh (có thể config trong application.properties)
    private static final String UPLOAD_DIR = "uploads/homestay-images/";
    
    /**
     * API upload ảnh homestay
     * POST http://localhost:8082/api/upload
     * 
     * Form Data:
     * - file: File ảnh cần upload
     * 
     * Response:
     * - Success: Trả về URL của ảnh
     * - Error: Trả về message lỗi
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("POST /api/upload - Uploading file: {}", file.getOriginalFilename());
            
            // Validate file
            if (file.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "File không được để trống");
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "File phải là ảnh (PNG, JPG, JPEG)");
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "File không được vượt quá 5MB");
                errorResponse.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Lưu file
            Path filePath = Paths.get(UPLOAD_DIR + uniqueFilename);
            Files.write(filePath, file.getBytes());
            
            // Tạo URL (trong production nên dùng CDN hoặc cloud storage)
            // Tạm thời trả về đường dẫn tương đối
            String imageUrl = "/uploads/homestay-images/" + uniqueFilename;
            
            log.info("File uploaded successfully: {}", imageUrl);
            
            // Trả về response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Upload ảnh thành công");
            response.put("data", imageUrl);
            response.put("url", imageUrl); // Thêm field url để frontend dễ parse
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Error uploading file", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi upload file: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error uploading file", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi không xác định: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
