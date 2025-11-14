package hbm.bookingservice.repository;

import hbm.bookingservice.dto.host.PaymentStatusCount;
import hbm.bookingservice.dto.payment.PaymentHistoryDto;
import hbm.bookingservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByOrderId(String orderId);

    @Query(value = """
    SELECT
        p.id,
        p.booking_id,
        p.amount,
        p.method,
        p.status AS paymentStatus,
        p.created_at,
        p.order_id,
        h.name AS homestayName,
        u.id AS userId
    FROM payment p
    JOIN booking b ON p.booking_id = b.id
    JOIN homestay h ON b.homestay_id = h.id
    JOIN user u ON b.user_id = u.id
    WHERE u.id = :userId
    ORDER BY p.created_at DESC;
    """, nativeQuery = true)
    List<PaymentHistoryDto> getPaymentHistoryByUserId(@Param("userId") Long userId);
    
    // Host Dashboard Methods
    
    /**
     * Đếm payment theo trạng thái cho host
     */
    @Query("""
        SELECT p.status as status, COUNT(p.id) as count
        FROM Payment p
        JOIN Booking b ON p.bookingId = b.id
        JOIN Homestay h ON b.homestayId = h.id
        WHERE h.userId = :hostId
        GROUP BY p.status
    """)
    List<PaymentStatusCount> countPaymentsByStatusForHost(@Param("hostId") Long hostId);
}
