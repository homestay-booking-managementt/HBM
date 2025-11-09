package hbm.bookingservice.repository;

import hbm.bookingservice.dto.booking.BookingDetailProjection;
import hbm.bookingservice.dto.booking.BookingSummaryProjection;
import hbm.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
            b.created_at AS createdAt,
            h.id AS homestayId,
            h.name AS homestayName,
            h.city AS homestayCity,
            hi.url AS primaryImageUrl
        FROM booking b
        JOIN homestay h ON b.homestay_id = h.id
        LEFT JOIN homestay_image hi ON h.id = hi.homestay_id AND hi.is_primary = 1
        WHERE b.user_id = :userId
        ORDER BY b.user_id,b.created_at DESC
    """, nativeQuery = true)
    List<BookingSummaryProjection> findMyBookingsByUserId(@Param("userId") Long userId);

    @Query(value = """
    SELECT
        b.id AS bookingId, b.check_in AS checkIn, b.check_out AS checkOut,
        b.total_price AS totalPrice, b.status AS status, b.nights AS nights, b.created_at AS createdAt,
        h.id AS homestayId, h.name AS homestayName, h.description AS description,
        h.address AS address, h.city AS city, h.lat AS lat, h.long AS longVal,
        h.base_price AS basePrice, h.amenities AS amenities,
        h.capacity AS capacity, h.num_rooms AS numRooms, h.bathroom_count AS bathroomCount,
        u.id AS userId, u.name AS userName, u.email AS userEmail, u.phone AS userPhone,
        hi.id AS homestayImageId, hi.url AS imageUrl, hi.is_primary AS isPrimary
    FROM booking b
    JOIN homestay h ON b.homestay_id = h.id
    JOIN user u ON b.user_id = u.id
    LEFT JOIN homestay_image hi ON h.id = hi.homestay_id
    WHERE b.id = :bookingId AND b.user_id = :userId
    """, nativeQuery = true)
    List<BookingDetailProjection> findBookingDetails(@Param("bookingId") Long bookingId, @Param("userId") Long userId);
}
