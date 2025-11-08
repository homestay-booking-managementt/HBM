package hbm.homestayservice.repository;

import hbm.homestayservice.entity.HomestayImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomestayImageRepository extends JpaRepository<HomestayImage, Long> {
    
    /**
     * Lấy tất cả ảnh của một homestay
     */
    List<HomestayImage> findByHomestayIdOrderByIsPrimaryDesc(Long homestayId);
    
    /**
     * Xóa tất cả ảnh của một homestay
     */
    void deleteByHomestayId(Long homestayId);
}
