package hbm.bookingservice.controller;

import hbm.bookingservice.dto.booking.BookingCreationRequestDto;
import hbm.bookingservice.dto.booking.BookingDetailDto;
import hbm.bookingservice.dto.booking.BookingDto;
import hbm.bookingservice.dto.booking.BookingStatusUpdateDto;
import hbm.bookingservice.dto.user.CustomerBookingCancelDto;
import hbm.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getMyBookings(
            @RequestParam(name = "userId") Long userId) {

        List<BookingDto> bookings = bookingService.getMyBookings(userId);

        if (bookings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailDto> getBookingDetails(
            @PathVariable Long bookingId,
            @RequestParam(name = "userId") Long userId) {

        BookingDetailDto detail = bookingService.getBookingDetails(bookingId, userId);

        if (detail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(detail, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BookingDetailDto> createBooking(
            @Valid @RequestBody BookingCreationRequestDto requestDto) {

        try {
            BookingDetailDto newBooking = bookingService.createBooking(requestDto);

            // Trả về 201 Created
            return new ResponseEntity<>(newBooking, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Xử lý lỗi nghiệp vụ (ngày tháng không hợp lệ, vv)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 BAD REQUEST
        } catch (RuntimeException e) {
            // Xử lý lỗi server (lỗi kết nối, homestay không khả dụng)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 INTERNAL SERVER ERROR
        }
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingDetailDto> updateBookingStatus(
            // Lấy ID Host từ Security Context (Giả định đã có Filter/Interceptor)
            @RequestParam("hostId") Long hostId,
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingStatusUpdateDto updateDto) {

            BookingDetailDto updatedBooking = bookingService.updateBookingStatusByHost(
                    bookingId, hostId, updateDto.getNewStatus());

            return ResponseEntity.ok(updatedBooking);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingDetailDto> cancelBooking(
            @RequestParam("userId") Long customerId, // Lấy ID Customer từ Security Context
            @PathVariable Long bookingId,
            @Valid @RequestBody CustomerBookingCancelDto cancelDto) {

            BookingDetailDto updatedBooking = bookingService.cancelBookingByCustomer(
                    bookingId,
                    customerId,
                    cancelDto.getCancellationReason());

            return ResponseEntity.ok(updatedBooking);
    }
}
