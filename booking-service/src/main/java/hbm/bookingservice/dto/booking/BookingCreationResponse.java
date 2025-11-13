package hbm.bookingservice.dto.booking;

// Đặt trong package dto/booking (ví dụ)

import hbm.bookingservice.dto.payment.CreateMomoResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreationResponse {
    // Thông tin chi tiết Booking đã tạo
    private BookingDetailDto booking;

    // Thông tin phản hồi từ MoMo (chứa payUrl, qrCodeUrl)
    private CreateMomoResponse momoResponse;
}
