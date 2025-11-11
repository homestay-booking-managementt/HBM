package hbm.bookingservice.repository;

import hbm.bookingservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Kiểm tra xem Booking đã có Review chưa
    boolean existsByBookingIdAndIsDeletedFalse(Long bookingId);

    // Lấy danh sách Review theo Homestay ID (phục vụ cho màn hình chi tiết Homestay)
    @Query("SELECT r FROM Review r WHERE r.homestayId = :homestayId AND r.isDeleted = false ORDER BY r.createdAt DESC")
    List<Review> findByHomestayIdAndIsDeletedFalse(Long homestayId);
}
