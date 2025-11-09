package hbm.adminservice.repository;

import hbm.adminservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Lấy tất cả booking, sắp xếp theo thời gian tạo mới nhất
     */
    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findAllBookingsOrderByCreatedAtDesc();
    
    /**
     * Lấy booking theo status
     */
    @Query("SELECT b FROM Booking b WHERE b.status = :status ORDER BY b.createdAt DESC")
    List<Booking> findByStatusOrderByCreatedAtDesc(@Param("status") String status);
    
    /**
     * Lấy booking theo user ID
     */
    @Query("SELECT b FROM Booking b WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    List<Booking> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * Lấy booking theo homestay ID
     */
    @Query("SELECT b FROM Booking b WHERE b.homestayId = :homestayId ORDER BY b.createdAt DESC")
    List<Booking> findByHomestayIdOrderByCreatedAtDesc(@Param("homestayId") Long homestayId);
    
    // ========== Revenue Report Queries ==========
    
    /**
     * Đếm số booking theo status
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") String status);
    
    /**
     * Tổng doanh thu theo status (trừ cancelled)
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status != :excludeStatus")
    BigDecimal sumTotalPriceByStatusNotIn(@Param("excludeStatus") String excludeStatus);
    
    /**
     * Tổng doanh thu theo status cụ thể
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = :status")
    BigDecimal sumTotalPriceByStatus(@Param("status") String status);
    
    /**
     * Đếm số homestay có booking (distinct)
     */
    @Query("SELECT COUNT(DISTINCT b.homestayId) FROM Booking b")
    Long countDistinctHomestayId();
    
    /**
     * Đếm số user có booking (distinct)
     */
    @Query("SELECT COUNT(DISTINCT b.userId) FROM Booking b WHERE b.userId IS NOT NULL")
    Long countDistinctUserId();
    
    // ========== Date Range Queries ==========
    
    /**
     * Đếm booking trong khoảng thời gian
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * Đếm booking theo status và khoảng thời gian
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status AND b.createdAt BETWEEN :startDate AND :endDate")
    Long countByStatusAndCreatedAtBetween(@Param("status") String status,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    /**
     * Tổng doanh thu (trừ status) trong khoảng thời gian
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status != :excludeStatus AND b.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalPriceByStatusNotInAndCreatedAtBetween(@Param("excludeStatus") String excludeStatus,
                                                               @Param("startDate") LocalDateTime startDate,
                                                               @Param("endDate") LocalDateTime endDate);
    
    /**
     * Tổng doanh thu theo status trong khoảng thời gian
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = :status AND b.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalPriceByStatusAndCreatedAtBetween(@Param("status") String status,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Đếm số homestay có booking trong khoảng thời gian
     */
    @Query("SELECT COUNT(DISTINCT b.homestayId) FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    Long countDistinctHomestayIdByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Đếm số user có booking trong khoảng thời gian
     */
    @Query("SELECT COUNT(DISTINCT b.userId) FROM Booking b WHERE b.userId IS NOT NULL AND b.createdAt BETWEEN :startDate AND :endDate")
    Long countDistinctUserIdByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
}
