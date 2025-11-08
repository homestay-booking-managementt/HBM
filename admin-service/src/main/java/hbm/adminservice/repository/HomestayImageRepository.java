package hbm.adminservice.repository;

import hbm.adminservice.entity.HomestayImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomestayImageRepository extends JpaRepository<HomestayImage, Long> {
    
    /**
     * Lấy tất cả ảnh của một homestay (sắp xếp ảnh primary lên đầu)
     */
    List<HomestayImage> findByHomestayIdOrderByIsPrimaryDesc(Long homestayId);
}
