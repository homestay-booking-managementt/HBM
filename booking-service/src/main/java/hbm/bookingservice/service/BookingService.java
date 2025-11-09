package hbm.bookingservice.service;

import hbm.bookingservice.dto.booking.BookingCreationRequestDto;
import hbm.bookingservice.dto.booking.BookingDetailDto;
import hbm.bookingservice.dto.booking.BookingDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getMyBookings(Long userId);

    BookingDetailDto getBookingDetails(Long bookingId, Long userId);

    BookingDetailDto createBooking(BookingCreationRequestDto bookingDto);
}
