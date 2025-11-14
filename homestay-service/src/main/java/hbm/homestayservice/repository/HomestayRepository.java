package hbm.homestayservice.repository;

import hbm.homestayservice.entity.Homestay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long> {
    
    /**
     * Lấy danh sách homestay công khai với các bộ lọc
     */
    @Query("SELECT DISTINCT h FROM Homestay h " +
           "WHERE h.status = 2 AND h.isDeleted = false " +
           "AND (:city IS NULL OR h.city = :city) " +
           "AND (:capacity IS NULL OR h.capacity >= :capacity) " +
           "AND ((:checkIn IS NULL OR :checkOut IS NULL) OR " +
           "     h.id NOT IN (SELECT b.homestayId FROM Booking b " +
           "                  WHERE b.checkOut > :checkIn AND b.checkIn < :checkOut " +
           "                  AND b.status IN ('confirmed', 'pending')))")
    List<Homestay> findPublicHomestaysWithFilters(
            @Param("city") String city,
            @Param("capacity") Short capacity,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
    
    /**
     * Lấy danh sách homestay của chủ nhà (theo user_id)
     */
    @Query("SELECT h FROM Homestay h WHERE h.userId = :userId AND h.isDeleted = false ORDER BY h.createdAt DESC")
    List<Homestay> findByUserId(@Param("userId") Long userId);
    
    /**
     * Simple test query - select all public homestays without subquery
     */
    @Query("SELECT h FROM Homestay h WHERE h.status = 2 AND h.isDeleted = false")
    List<Homestay> findAllPublicSimple();
    
    /**
     * Lấy danh sách homestay theo owner ID (không bao gồm đã xóa)
     */
    List<Homestay> findByUserIdAndIsDeletedFalse(Long userId);
}
