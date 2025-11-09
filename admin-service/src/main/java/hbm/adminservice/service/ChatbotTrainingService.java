package hbm.adminservice.service;

import hbm.adminservice.dto.ChatbotTrainingDTO;
import hbm.adminservice.dto.CreateChatbotTrainingRequest;
import hbm.adminservice.dto.UpdateChatbotTrainingRequest;
import hbm.adminservice.entity.ChatbotTraining;
import hbm.adminservice.repository.ChatbotTrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotTrainingService {
    
    private final ChatbotTrainingRepository chatbotTrainingRepository;
    
    /**
     * Lấy danh sách tất cả chatbot training
     */
    public List<ChatbotTrainingDTO> getAllChatbotTrainings(Boolean isActive, String category, String keyword) {
        List<ChatbotTraining> trainings;
        
        // Tìm kiếm theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            trainings = chatbotTrainingRepository.searchByQuestionPattern(keyword);
        }
        // Lọc theo category và isActive
        else if (category != null && !category.trim().isEmpty() && isActive != null) {
            trainings = chatbotTrainingRepository.findByCategoryAndIsActiveOrderByCreatedAtDesc(category, isActive);
        }
        // Lọc theo category
        else if (category != null && !category.trim().isEmpty()) {
            trainings = chatbotTrainingRepository.findByCategoryOrderByCreatedAtDesc(category);
        }
        // Lọc theo isActive
        else if (isActive != null) {
            trainings = chatbotTrainingRepository.findByIsActiveOrderByCreatedAtDesc(isActive);
        }
        // Lấy tất cả
        else {
            trainings = chatbotTrainingRepository.findAllOrderByCreatedAtDesc();
        }
        
        return trainings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy chi tiết chatbot training theo ID
     */
    public Optional<ChatbotTrainingDTO> getChatbotTrainingById(Long id) {
        return chatbotTrainingRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    /**
     * Tạo mới chatbot training
     */
    @Transactional
    public ChatbotTrainingDTO createChatbotTraining(CreateChatbotTrainingRequest request) {
        ChatbotTraining training = new ChatbotTraining();
        training.setQuestionPattern(request.getQuestionPattern());
        training.setAnswerTemplate(request.getAnswerTemplate());
        training.setCategory(request.getCategory());
        training.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        ChatbotTraining savedTraining = chatbotTrainingRepository.save(training);
        return convertToDTO(savedTraining);
    }
    
    /**
     * Cập nhật chatbot training
     */
    @Transactional
    public Optional<ChatbotTrainingDTO> updateChatbotTraining(Long id, UpdateChatbotTrainingRequest request) {
        Optional<ChatbotTraining> optionalTraining = chatbotTrainingRepository.findById(id);
        
        if (optionalTraining.isEmpty()) {
            return Optional.empty();
        }
        
        ChatbotTraining training = optionalTraining.get();
        
        if (request.getQuestionPattern() != null) {
            training.setQuestionPattern(request.getQuestionPattern());
        }
        if (request.getAnswerTemplate() != null) {
            training.setAnswerTemplate(request.getAnswerTemplate());
        }
        if (request.getCategory() != null) {
            training.setCategory(request.getCategory());
        }
        if (request.getIsActive() != null) {
            training.setIsActive(request.getIsActive());
        }
        
        ChatbotTraining updatedTraining = chatbotTrainingRepository.save(training);
        return Optional.of(convertToDTO(updatedTraining));
    }
    
    /**
     * Xóa chatbot training
     */
    @Transactional
    public boolean deleteChatbotTraining(Long id) {
        if (chatbotTrainingRepository.existsById(id)) {
            chatbotTrainingRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Convert Entity sang DTO
     */
    private ChatbotTrainingDTO convertToDTO(ChatbotTraining training) {
        ChatbotTrainingDTO dto = new ChatbotTrainingDTO();
        dto.setId(training.getId());
        dto.setQuestionPattern(training.getQuestionPattern());
        dto.setAnswerTemplate(training.getAnswerTemplate());
        dto.setCategory(training.getCategory());
        dto.setIsActive(training.getIsActive());
        dto.setCreatedAt(training.getCreatedAt());
        dto.setUpdatedAt(training.getUpdatedAt());
        return dto;
    }
}
