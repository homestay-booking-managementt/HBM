package hbm.adminservice.service;

import hbm.adminservice.dto.AdminUpdateStatusRequest;
import hbm.adminservice.dto.HomestayDTO;
import hbm.adminservice.entity.Homestay;
import hbm.adminservice.repository.HomestayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminHomestayService {
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    /**
     * Admin duyệt/tạm ẩn/khóa homestay
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
        if (Boolean.TRUE.equals(homestay.getIsDeleted())) {
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
