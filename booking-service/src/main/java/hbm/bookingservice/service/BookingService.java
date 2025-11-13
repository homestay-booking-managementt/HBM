package hbm.bookingservice.service;

import hbm.bookingservice.dto.booking.BookingCreationRequestDto;
import hbm.bookingservice.dto.booking.BookingCreationResponse;
import hbm.bookingservice.dto.booking.BookingDetailDto;
import hbm.bookingservice.dto.booking.BookingDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getMyBookings(Long userId);

    BookingDetailDto getBookingDetails(Long bookingId, Long userId);

    BookingCreationResponse createBooking(BookingCreationRequestDto bookingDto);

    BookingDetailDto updateBookingStatusByHost(Long bookingId, Long hostId, String newStatus);

    BookingDetailDto cancelBookingByCustomer(Long bookingId, Long customerId, String reason);
}
