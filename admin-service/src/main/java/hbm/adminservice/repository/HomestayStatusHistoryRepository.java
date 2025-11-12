package hbm.adminservice.repository;

import hbm.adminservice.entity.HomestayStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomestayStatusHistoryRepository extends JpaRepository<HomestayStatusHistory, Long> {
    
    /**
     * Lấy lịch sử thay đổi trạng thái của homestay
     * Sắp xếp theo thời gian mới nhất
     */
    @Query("SELECT h FROM HomestayStatusHistory h WHERE h.homestayId = :homestayId ORDER BY h.changedAt DESC")
    List<HomestayStatusHistory> findByHomestayIdOrderByChangedAtDesc(@Param("homestayId") Long homestayId);
    
    /**
     * Lấy lịch sử thay đổi trạng thái với thông tin người thay đổi
     */
    @Query(value = "SELECT hsh.id, hsh.homestay_id, hsh.old_status, hsh.new_status, " +
            "hsh.reason, hsh.changed_by, hsh.changed_at, u.name as changed_by_name, u.email as changed_by_email " +
            "FROM homestay_status_history hsh " +
            "LEFT JOIN user u ON hsh.changed_by = u.id " +
            "WHERE hsh.homestay_id = :homestayId " +
            "ORDER BY hsh.changed_at DESC",
            nativeQuery = true)
    List<Object[]> findByHomestayIdWithUserInfo(@Param("homestayId") Long homestayId);
}
