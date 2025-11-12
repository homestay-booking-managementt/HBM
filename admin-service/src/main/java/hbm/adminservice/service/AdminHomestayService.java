package hbm.adminservice.service;

import hbm.adminservice.dto.AdminUpdateStatusRequest;
import hbm.adminservice.dto.HomestayDTO;
import hbm.adminservice.entity.Homestay;
import hbm.adminservice.entity.HomestayStatusHistory;
import hbm.adminservice.repository.HomestayRepository;
import hbm.adminservice.repository.HomestayStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminHomestayService {
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    @Autowired
    private HomestayStatusHistoryRepository homestayStatusHistoryRepository;
    
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
     * Lấy toàn bộ danh sách homestay (bao gồm cả homestay bị ẩn, khóa)
     * Chỉ admin mới được phép xem toàn bộ
     */
    public java.util.List<HomestayDTO> getAllHomestaysForAdmin() {
        java.util.List<Homestay> homestays = homestayRepository.findAllHomestaysForAdmin();
        return homestays.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Cập nhật trạng thái homestay (đơn giản hóa)
     * Status: 0 = Inactive, 1 = Active, 2 = Pending, 3 = Banned
     */
    @Transactional
    public HomestayDTO updateHomestayStatus(Long homestayId, Integer status, String reason) {
        if (homestayId == null) {
            throw new IllegalArgumentException("Homestay ID không được để trống");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("Status không được để trống");
        }
        
        if (status < 0 || status > 3) {
            throw new IllegalArgumentException("Status không hợp lệ. Cho phép: 0 (Inactive), 1 (Active), 2 (Pending), 3 (Banned)");
        }
        
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy homestay với ID: " + homestayId));
        
        if (Boolean.TRUE.equals(homestay.getIsDeleted())) {
            throw new IllegalArgumentException("Homestay đã bị xóa");
        }
        
        // Lưu trạng thái cũ trước khi cập nhật (convert Byte to Integer)
        Integer oldStatus = homestay.getStatus() != null ? homestay.getStatus().intValue() : null;
        
        // Cập nhật trạng thái mới (convert Integer to Byte)
        homestay.setStatus(status.byteValue());
        homestay.setUpdatedAt(LocalDateTime.now());
        
        Homestay updatedHomestay = homestayRepository.save(homestay);
        
        // Lưu lịch sử thay đổi trạng thái
        if (!status.equals(oldStatus)) {
            HomestayStatusHistory history = new HomestayStatusHistory();
            history.setHomestayId(homestayId);
            history.setOldStatus(oldStatus);
            history.setNewStatus(status);
            history.setChangedAt(LocalDateTime.now());
            history.setReason(reason != null && !reason.trim().isEmpty() ? reason : "Admin cập nhật trạng thái");
            // TODO: Get current admin user ID from security context
            // history.setChangedBy(currentAdminId);
            
            homestayStatusHistoryRepository.save(history);
        }
        
        return convertToDTO(updatedHomestay);
    }
    
    /**
     * Lấy chi tiết homestay
     */
    public HomestayDTO getHomestayDetail(Long homestayId) {
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy homestay với ID: " + homestayId));
        
        return convertToDTO(homestay);
    }
    
    /**
     * Lấy lịch sử thay đổi trạng thái của homestay
     */
    public java.util.List<java.util.Map<String, Object>> getHomestayStatusHistory(Long homestayId) {
        // Kiểm tra homestay tồn tại
        Homestay homestay = homestayRepository.findById(homestayId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy homestay với ID: " + homestayId));
        
        // Lấy lịch sử từ database với thông tin user (sử dụng native query có JOIN)
        java.util.List<Object[]> historyResults = 
                homestayStatusHistoryRepository.findByHomestayIdWithUserInfo(homestayId);
        
        // Chuyển đổi sang Map
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        
        for (Object[] row : historyResults) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", row[0]);
            item.put("homestayId", row[1]);
            item.put("oldStatus", row[2]);
            item.put("newStatus", row[3]);
            item.put("reason", row[4]);
            item.put("changedBy", row[5]);
            item.put("changedAt", row[6]);
            item.put("changedByName", row[7]);
            item.put("changedByEmail", row[8]);
            
            result.add(item);
        }
        
        // Nếu không có lịch sử, thêm trạng thái hiện tại
        if (result.isEmpty()) {
            java.util.Map<String, Object> currentStatus = new java.util.HashMap<>();
            currentStatus.put("id", null);
            currentStatus.put("homestayId", homestay.getId());
            currentStatus.put("oldStatus", null);
            currentStatus.put("newStatus", homestay.getStatus());
            currentStatus.put("changedAt", homestay.getCreatedAt());
            currentStatus.put("changedBy", homestay.getUserId());
            currentStatus.put("reason", "Trạng thái khởi tạo");
            currentStatus.put("changedByName", null);
            currentStatus.put("changedByEmail", null);
            
            result.add(currentStatus);
        }
        
        return result;
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
