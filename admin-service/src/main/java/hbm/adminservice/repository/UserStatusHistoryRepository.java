package hbm.adminservice.repository;

import hbm.adminservice.entity.UserStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatusHistoryRepository extends JpaRepository<UserStatusHistory, Long> {
    
    @Query(value = "SELECT h.*, u.name as changed_by_name, u.email as changed_by_email " +
                   "FROM user_status_history h " +
                   "LEFT JOIN user u ON h.changed_by = u.id " +
                   "WHERE h.user_id = :userId " +
                   "ORDER BY h.changed_at DESC",
           nativeQuery = true)
    List<Object[]> findByUserIdWithChangedByInfo(@Param("userId") Long userId);
}
