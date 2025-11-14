package hbm.bookingservice.repository;

import hbm.bookingservice.dto.booking.BookingDetailProjection;
import hbm.bookingservice.dto.booking.BookingSummaryProjection;
import hbm.bookingservice.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = """ 
        SELECT
            b.id AS bookingId,
            b.check_in AS checkIn,
            b.check_out AS checkOut,
            b.nights AS nights,
            b.total_price AS totalPrice,
            b.status AS status,
            p.status AS paymentStatus,
            b.created_at AS createdAt,
            h.id AS homestayId,
            h.name AS homestayName,
            h.city AS homestayCity,
            hi.url AS primaryImageUrl
        FROM booking b
        JOIN homestay h ON b.homestay_id = h.id
        JOIN payment p on b.id = p.booking_id
        LEFT JOIN homestay_image hi ON h.id = hi.homestay_id AND hi.is_primary = 1
        WHERE b.user_id = :userId
        ORDER BY b.user_id,b.created_at DESC
    """, nativeQuery = true)
    List<BookingSummaryProjection> findMyBookingsByUserId(@Param("userId") Long userId);

    @Query(value = """
    SELECT
        b.id AS bookingId, b.check_in AS checkIn, b.check_out AS checkOut,
        b.total_price AS totalPrice, b.status AS status, p.status AS paymentStatus,p.pay_url as payUrl,
        b.nights AS nights, b.created_at AS createdAt,
        h.id AS homestayId, h.name AS homestayName, h.description AS description,
        h.address AS address, h.city AS city, h.lat AS lat, h.long AS longVal,
        h.base_price AS basePrice, h.amenities AS amenities,
        h.capacity AS capacity, h.num_rooms AS numRooms, h.bathroom_count AS bathroomCount,
        u.id AS userId, u.name AS userName, u.email AS userEmail, u.phone AS userPhone,
        hi.id AS homestayImageId, hi.url AS imageUrl, hi.is_primary AS isPrimary
    FROM booking b
    JOIN homestay h ON b.homestay_id = h.id
    JOIN user u ON b.user_id = u.id
    JOIN payment p on b.id = p.booking_id
    LEFT JOIN homestay_image hi ON h.id = hi.homestay_id
    WHERE b.id = :bookingId AND b.user_id = :userId
    """, nativeQuery = true)
    List<BookingDetailProjection> findBookingDetails(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

    @Query(value = """
    SELECT COUNT(b.id)
    FROM Booking b
    WHERE b.homestayId = :homestayId
      AND b.status IN ('pending_payment', 'confirmed', 'completed')
      AND b.id != :currentBookingId
      AND (
          (b.checkIn < :checkOut AND b.checkOut > :checkIn)
      )
    """)
    Long countConflictingConfirmedBookings(
            @Param("homestayId") Long homestayId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("currentBookingId") Long currentBookingId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT b FROM Booking b
    WHERE b.homestayId = :homestayId
      AND b.status IN ('pending_payment', 'confirmed', 'checked_in')
      AND (b.checkIn < :checkOut AND b.checkOut > :checkIn)
    """)
    List<Booking> findConflictingBookingsWithLock(
            @Param("homestayId") Long homestayId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    List<Booking> findAllByStatusAndPaymentDeadlineBefore(String status, LocalDateTime time);

    /**
     * Lấy danh sách booking theo customer ID (user_id)
     * Sắp xếp theo thời gian tạo mới nhất
     */
    @Query(value = "SELECT * FROM booking WHERE user_id = :customerId ORDER BY created_at DESC", nativeQuery = true)
    List<Booking> findByCustomerId(@Param("customerId") Long customerId);

}
