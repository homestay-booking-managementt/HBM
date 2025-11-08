package hbm.adminservice.repository;

import hbm.adminservice.entity.ChatbotTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotTrainingRepository extends JpaRepository<ChatbotTraining, Long> {
    
    /**
     * Lấy tất cả chatbot training, sắp xếp theo thời gian tạo mới nhất
     */
    @Query("SELECT c FROM ChatbotTraining c ORDER BY c.createdAt DESC")
    List<ChatbotTraining> findAllOrderByCreatedAtDesc();
    
    /**
     * Lấy chatbot training theo trạng thái active
     */
    @Query("SELECT c FROM ChatbotTraining c WHERE c.isActive = :isActive ORDER BY c.createdAt DESC")
    List<ChatbotTraining> findByIsActiveOrderByCreatedAtDesc(@Param("isActive") Boolean isActive);
    
    /**
     * Lấy chatbot training theo category
     */
    @Query("SELECT c FROM ChatbotTraining c WHERE c.category = :category ORDER BY c.createdAt DESC")
    List<ChatbotTraining> findByCategoryOrderByCreatedAtDesc(@Param("category") String category);
    
    /**
     * Lấy chatbot training theo category và trạng thái active
     */
    @Query("SELECT c FROM ChatbotTraining c WHERE c.category = :category AND c.isActive = :isActive ORDER BY c.createdAt DESC")
    List<ChatbotTraining> findByCategoryAndIsActiveOrderByCreatedAtDesc(@Param("category") String category, @Param("isActive") Boolean isActive);
    
    /**
     * Tìm kiếm theo question pattern (LIKE)
     */
    @Query("SELECT c FROM ChatbotTraining c WHERE LOWER(c.questionPattern) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY c.createdAt DESC")
    List<ChatbotTraining> searchByQuestionPattern(@Param("keyword") String keyword);
}
