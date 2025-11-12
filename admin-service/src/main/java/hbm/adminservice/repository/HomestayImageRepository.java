package hbm.adminservice.repository;

import hbm.adminservice.entity.HomestayImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomestayImageRepository extends JpaRepository<HomestayImage, Long> {
    
    /**
     * Lấy danh sách ảnh của homestay, sắp xếp ảnh chính lên đầu
     */
    List<HomestayImage> findByHomestayIdOrderByIsPrimaryDesc(Long homestayId);
}
