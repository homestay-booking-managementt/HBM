package hbm.adminservice.repository;

import hbm.adminservice.entity.Homestay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long> {
    
    /**
     * Lấy danh sách homestay chờ duyệt (status = 1)
     * Sắp xếp theo thời gian tạo mới nhất
     */
    @Query("SELECT h FROM Homestay h WHERE h.status = 1 AND h.isDeleted = false ORDER BY h.createdAt DESC")
    List<Homestay> findPendingHomestays();
    
    /**
     * Lấy toàn bộ danh sách homestay (bao gồm cả homestay bị ẩn, khóa)
     * Chỉ loại bỏ homestay đã bị xóa (isDeleted = true)
     * Sắp xếp theo thời gian tạo mới nhất
     */
    @Query("SELECT h FROM Homestay h WHERE h.isDeleted = false ORDER BY h.createdAt DESC")
    List<Homestay> findAllHomestaysForAdmin();
}
