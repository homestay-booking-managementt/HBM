package hbm.homestayservice.service;

import hbm.homestayservice.dto.AdminUpdateStatusRequest;
import hbm.homestayservice.dto.CreateHomestayRequest;
import hbm.homestayservice.dto.HomestayDTO;
import hbm.homestayservice.dto.UpdateHomestayStatusRequest;
import hbm.homestayservice.entity.Homestay;
import hbm.homestayservice.repository.HomestayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomestayService {
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    /**
     * Lấy danh sách homestay công khai với các bộ lọc
     */
    public List<HomestayDTO> getPublicHomestays(String city, Short capacity, LocalDate checkIn, LocalDate checkOut) {
        List<Homestay> homestays = homestayRepository.findPublicHomestaysWithFilters(city, capacity, checkIn, checkOut);
        return homestays.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Tạo homestay mới với status = 1 (chờ duyệt)
     */
    @Transactional
    public HomestayDTO createHomestay(CreateHomestayRequest request) {
        // Validate dữ liệu
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên homestay không được để trống");
        }
        
        if (request.getBasePrice() == null || request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá phải lớn hơn 0");
        }
        
        // Tạo entity mới
        Homestay homestay = new Homestay();
        homestay.setUserId(request.getUserId());
        homestay.setName(request.getName());
        homestay.setDescription(request.getDescription());
        homestay.setAddress(request.getAddress());
        homestay.setCity(request.getCity());
        homestay.setLat(request.getLat());
        homestay.setLongitude(request.getLongitude());
        homestay.setCapacity(request.getCapacity() != null ? request.getCapacity() : 2);
        homestay.setNumRooms(request.getNumRooms() != null ? request.getNumRooms() : 1);
        homestay.setBathroomCount(request.getBathroomCount() != null ? request.getBathroomCount() : 1);
        homestay.setBasePrice(request.getBasePrice());
        homestay.setAmenities(request.getAmenities());
        
        // Set status = 1 (chờ duyệt)
        homestay.setStatus((byte) 1);
        
        // Không set created_at, updated_at, is_deleted
        // Các field này sẽ được database tự động set với DEFAULT values
        // Không set approved_by và approved_at (để null)
        homestay.setApprovedBy(null);
        homestay.setApprovedAt(null);
        
        // Lưu vào database
        Homestay savedHomestay = homestayRepository.save(homestay);
        
        return convertToDTO(savedHomestay);
    }
    
    /**
     * Lấy danh sách homestay của chủ nhà hiện tại
     */
    public List<HomestayDTO> getMyHomestays(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được để trống");
        }
        
        List<Homestay> homestays = homestayRepository.findByUserId(userId);
        return homestays.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Chuyển đổi trạng thái homestay (chỉ chủ nhà mới được thay đổi homestay của mình)
     * Status: 2 = công khai, 3 = tạm ẩn, 4 = bị khóa
     */
    @Transactional
    public HomestayDTO updateHomestayStatus(Long homestayId, Long userId, UpdateHomestayStatusRequest request) {
        // Validate
        if (homestayId == null) {
            throw new IllegalArgumentException("Homestay ID không được để trống");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID không được để trống");
        }
        
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status không được để trống");
        }
        
        // Kiểm tra status hợp lệ (2, 3, 4)
        if (request.getStatus() < 2 || request.getStatus() > 4) {
            throw new IllegalArgumentException("Status không hợp lệ. Chỉ cho phép: 2 (công khai), 3 (tạm ẩn), 4 (bị khóa)");
        }
        
        // Tìm homestay
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy homestay"));
        
        // Kiểm tra quyền sở hữu
        if (!homestay.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa homestay này");
        }
        
        // Kiểm tra homestay đã bị xóa chưa
        if (homestay.getIsDeleted()) {
            throw new IllegalArgumentException("Homestay đã bị xóa");
        }
        
        // Cập nhật status
        homestay.setStatus(request.getStatus());
        
        // Lưu thay đổi
        Homestay updatedHomestay = homestayRepository.save(homestay);
        
        return convertToDTO(updatedHomestay);
    }
    
    /**
     * Admin duyệt/khóa homestay
     * Status: 2 = duyệt & công khai, 3 = tạm ẩn, 4 = bị khóa
     * Chỉ admin mới được phép gọi API này
     */
    @Transactional
    public HomestayDTO adminUpdateStatus(Long homestayId, Long adminId, AdminUpdateStatusRequest request) {
        // Validate
        if (homestayId == null) {
            throw new IllegalArgumentException("Homestay ID không được để trống");
        }
        
        if (adminId == null) {
            throw new IllegalArgumentException("Admin ID không được để trống");
        }
        
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Status không được để trống");
        }
        
        // Kiểm tra status hợp lệ (2, 3, 4)
        if (request.getStatus() < 2 || request.getStatus() > 4) {
            throw new IllegalArgumentException("Status không hợp lệ. Admin chỉ cho phép: 2 (duyệt & công khai), 3 (tạm ẩn), 4 (bị khóa)");
        }
        
        // Tìm homestay
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy homestay"));
        
        // Kiểm tra homestay đã bị xóa chưa
        if (homestay.getIsDeleted()) {
            throw new IllegalArgumentException("Homestay đã bị xóa");
        }
        
        // Cập nhật status
        homestay.setStatus(request.getStatus());
        
        // Nếu admin duyệt (status = 2), cập nhật approved_by và approved_at
        if (request.getStatus() == 2) {
            homestay.setApprovedBy(adminId);
            homestay.setApprovedAt(LocalDateTime.now());
        }
        
        // Lưu thay đổi
        Homestay updatedHomestay = homestayRepository.save(homestay);
        
        return convertToDTO(updatedHomestay);
    }
    
    /**
     * Chuyển đổi Entity sang DTO
     */
    private HomestayDTO convertToDTO(Homestay homestay) {
        HomestayDTO dto = new HomestayDTO();
        dto.setId(homestay.getId());
        dto.setUserId(homestay.getUserId());
        dto.setName(homestay.getName());
        dto.setDescription(homestay.getDescription());
        dto.setAddress(homestay.getAddress());
        dto.setCity(homestay.getCity());
        dto.setLat(homestay.getLat());
        dto.setLongitude(homestay.getLongitude());
        dto.setCapacity(homestay.getCapacity());
        dto.setNumRooms(homestay.getNumRooms());
        dto.setBathroomCount(homestay.getBathroomCount());
        dto.setBasePrice(homestay.getBasePrice());
        dto.setAmenities(homestay.getAmenities());
        dto.setStatus(homestay.getStatus());
        dto.setCreatedAt(homestay.getCreatedAt());
        dto.setUpdatedAt(homestay.getUpdatedAt());
        return dto;
    }
}
