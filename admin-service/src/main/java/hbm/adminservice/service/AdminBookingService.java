package hbm.adminservice.service;

import hbm.adminservice.dto.BookingDTO;
import hbm.adminservice.dto.CustomerBookingsResponse;
import hbm.adminservice.entity.Booking;
import hbm.adminservice.entity.Homestay;
import hbm.adminservice.entity.User;
import hbm.adminservice.repository.BookingRepository;
import hbm.adminservice.repository.HomestayRepository;
import hbm.adminservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminBookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    /**
     * Lấy tất cả booking
     */
    public List<BookingDTO> getAllBookings(String status, Long userId, Long homestayId) {
        List<Booking> bookings;
        
        if (status != null && !status.trim().isEmpty()) {
            // Lọc theo status
            bookings = bookingRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (userId != null) {
            // Lọc theo user ID
            bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (homestayId != null) {
            // Lọc theo homestay ID
            bookings = bookingRepository.findByHomestayIdOrderByCreatedAtDesc(homestayId);
        } else {
            // Lấy tất cả
            bookings = bookingRepository.findAllBookingsOrderByCreatedAtDesc();
        }
        
        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy chi tiết booking theo ID
     */
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking với ID: " + id));
        
        return convertToDTO(booking);
    }
    
    /**
     * Lấy danh sách booking của một customer cụ thể kèm thông tin customer
     */
    public CustomerBookingsResponse getBookingsByCustomerId(Long customerId) {
        // Lấy thông tin customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));
        
        // Lấy danh sách booking của customer
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(customerId);
        
        // Convert sang DTO
        List<BookingDTO> bookingDTOs = bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // Tạo CustomerInfo
        CustomerBookingsResponse.CustomerInfo customerInfo = new CustomerBookingsResponse.CustomerInfo(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
        
        // Trả về response
        return new CustomerBookingsResponse(bookingDTOs, customerInfo);
    }
    
    /**
     * Cập nhật status của booking
     */
    public BookingDTO updateBookingStatus(Long id, String newStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking với ID: " + id));
        
        // Validate status
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status không được để trống");
        }
        
        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return convertToDTO(updatedBooking);
    }
    
    /**
     * Convert Booking entity sang DTO với thông tin bổ sung
     */
    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setHomestayId(booking.getHomestayId());
        dto.setCheckIn(booking.getCheckIn());
        dto.setCheckOut(booking.getCheckOut());
        dto.setNights(booking.getNights());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        
        // Lấy thông tin user
        if (booking.getUserId() != null) {
            Optional<User> userOpt = userRepository.findById(booking.getUserId());
            userOpt.ifPresent(user -> {
                dto.setUserName(user.getName());
                dto.setUserEmail(user.getEmail());
                dto.setUserPhone(user.getPhone());
            });
        }
        
        // Lấy thông tin homestay
        Optional<Homestay> homestayOpt = homestayRepository.findById(booking.getHomestayId());
        homestayOpt.ifPresent(homestay -> {
            dto.setHomestayName(homestay.getName());
            dto.setHomestayCity(homestay.getCity());
            dto.setHomestayAddress(homestay.getAddress());
            dto.setHomestayCapacity(homestay.getCapacity() != null ? homestay.getCapacity().intValue() : null);
            dto.setHomestayNumRooms(homestay.getNumRooms() != null ? homestay.getNumRooms().intValue() : null);
            dto.setHomestayBathroomCount(homestay.getBathroomCount() != null ? homestay.getBathroomCount().intValue() : null);
            dto.setHomestayBasePrice(homestay.getBasePrice());
            
            // Lấy thông tin chủ nhà (owner) từ homestay.userId
            Long ownerId = homestay.getUserId();
            if (ownerId != null) {
                try {
                    Optional<User> ownerOpt = userRepository.findById(ownerId);
                    if (ownerOpt.isPresent()) {
                        User owner = ownerOpt.get();
                        dto.setOwnerName(owner.getName());
                        dto.setOwnerEmail(owner.getEmail());
                        dto.setOwnerPhone(owner.getPhone());
                    }
                } catch (Exception e) {
                    // Log error but don't throw - owner info is optional
                    System.err.println("Cannot fetch owner info for homestay " + homestay.getId() + ": " + e.getMessage());
                }
            }
        });
        
        return dto;
    }
}
