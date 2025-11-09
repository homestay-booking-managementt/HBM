package hbm.adminservice.repository;

import hbm.adminservice.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    /**
     * Lấy tất cả complaint, sắp xếp theo thời gian mới nhất
     */
    @Query("SELECT c FROM Complaint c ORDER BY c.createdAt DESC")
    List<Complaint> findAllOrderByCreatedAtDesc();
    
    /**
     * Lấy complaint theo status
     */
    @Query("SELECT c FROM Complaint c WHERE c.status = :status ORDER BY c.createdAt DESC")
    List<Complaint> findByStatusOrderByCreatedAtDesc(@Param("status") String status);
    
    /**
     * Lấy complaint theo user ID
     */
    @Query("SELECT c FROM Complaint c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    List<Complaint> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * Lấy complaint được giao cho admin
     */
    @Query("SELECT c FROM Complaint c WHERE c.assignedAdminId = :adminId ORDER BY c.createdAt DESC")
    List<Complaint> findByAssignedAdminIdOrderByCreatedAtDesc(@Param("adminId") Long adminId);
    
    /**
     * Lấy complaint chưa được giao (pending)
     */
    @Query("SELECT c FROM Complaint c WHERE c.status = 'pending' AND c.assignedAdminId IS NULL ORDER BY c.createdAt DESC")
    List<Complaint> findUnassignedComplaints();
}
