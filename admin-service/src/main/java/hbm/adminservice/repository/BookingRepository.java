package hbm.adminservice.repository;

import hbm.adminservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
