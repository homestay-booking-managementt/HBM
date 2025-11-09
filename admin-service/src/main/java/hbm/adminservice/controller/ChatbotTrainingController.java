package hbm.adminservice.controller;

import hbm.adminservice.dto.ChatbotTrainingDTO;
import hbm.adminservice.dto.CreateChatbotTrainingRequest;
import hbm.adminservice.dto.UpdateChatbotTrainingRequest;
import hbm.adminservice.service.ChatbotTrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/chatbot-training")
@RequiredArgsConstructor
public class ChatbotTrainingController {
    
    private final ChatbotTrainingService chatbotTrainingService;
    
    /**
     * Lấy danh sách chatbot training
     * GET /api/admin/chatbot-training
     * Query params: isActive, category, keyword
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllChatbotTrainings(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ChatbotTrainingDTO> trainings = chatbotTrainingService.getAllChatbotTrainings(isActive, category, keyword);
            
            response.put("success", true);
            response.put("message", "Danh sách chatbot training");
            response.put("data", trainings);
            response.put("total", trainings.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách chatbot training: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Lấy chi tiết chatbot training theo ID
     * GET /api/admin/chatbot-training/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getChatbotTrainingById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatbotTrainingDTO> training = chatbotTrainingService.getChatbotTrainingById(id);
            
            if (training.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy chatbot training với ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "Chi tiết chatbot training");
            response.put("data", training.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy chi tiết chatbot training: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Tạo mới chatbot training
     * POST /api/admin/chatbot-training
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createChatbotTraining(
            @RequestBody CreateChatbotTrainingRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validation
            if (request.getQuestionPattern() == null || request.getQuestionPattern().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Question pattern không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getAnswerTemplate() == null || request.getAnswerTemplate().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Answer template không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            ChatbotTrainingDTO createdTraining = chatbotTrainingService.createChatbotTraining(request);
            
            response.put("success", true);
            response.put("message", "Tạo chatbot training thành công");
            response.put("data", createdTraining);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo chatbot training: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Cập nhật chatbot training
     * PUT /api/admin/chatbot-training/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateChatbotTraining(
            @PathVariable Long id,
            @RequestBody UpdateChatbotTrainingRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatbotTrainingDTO> updatedTraining = chatbotTrainingService.updateChatbotTraining(id, request);
            
            if (updatedTraining.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy chatbot training với ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "Cập nhật chatbot training thành công");
            response.put("data", updatedTraining.get());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật chatbot training: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Xóa chatbot training
     * DELETE /api/admin/chatbot-training/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteChatbotTraining(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = chatbotTrainingService.deleteChatbotTraining(id);
            
            if (!deleted) {
                response.put("success", false);
                response.put("message", "Không tìm thấy chatbot training với ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "Xóa chatbot training thành công");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa chatbot training: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
