package hbm.adminservice.service;

import hbm.adminservice.dto.AdminUpdateStatusRequest;
import hbm.adminservice.dto.HomestayDTO;
import hbm.adminservice.dto.HomestayImageDTO;
import hbm.adminservice.entity.Homestay;
import hbm.adminservice.entity.HomestayImage;
import hbm.adminservice.entity.HomestayStatusHistory;
import hbm.adminservice.entity.User;
import hbm.adminservice.repository.HomestayImageRepository;
import hbm.adminservice.repository.HomestayRepository;
import hbm.adminservice.repository.HomestayStatusHistoryRepository;
import hbm.adminservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AdminHomestayService {
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    @Autowired
    private HomestayStatusHistoryRepository homestayStatusHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HomestayImageRepository homestayImageRepository;
    
    @Autowired
    private hbm.adminservice.repository.HomestayPendingRepository homestayPendingRepository;
    
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
     * Lấy danh sách homestay có yêu cầu cập nhật đang chờ duyệt
     * Lấy từ bảng homestay_pending với status='waiting'
     */
    public java.util.List<java.util.Map<String, Object>> getHomestaysPendingUpdate() {
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        
        try {
            // Lấy danh sách pending requests với status='waiting'
            java.util.List<hbm.adminservice.entity.HomestayPending> pendingList = 
                    homestayPendingRepository.findByStatusOrderBySubmittedAtAsc("waiting");
            
            System.out.println("🟡 [Pending Update] Found " + pendingList.size() + " pending requests");
            
            for (hbm.adminservice.entity.HomestayPending pending : pendingList) {
                // Lấy thông tin homestay hiện tại
                homestayRepository.findById(pending.getHomestayId()).ifPresent(homestay -> {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    
                    // Thông tin homestay hiện tại
                    HomestayDTO currentHomestay = convertToDTO(homestay);
                    item.put("homestay", currentHomestay);
                    
                    // Thông tin pending request
                    item.put("pendingId", pending.getId());
                    item.put("pendingData", pending.getPendingData());
                    item.put("submittedAt", pending.getSubmittedAt());
                    item.put("status", pending.getStatus());
                    
                    result.add(item);
                });
            }
        } catch (Exception e) {
            System.err.println("🔴 [Pending Update] Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
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
        
        if (status < 1 || status > 4) {
            throw new IllegalArgumentException("Status không hợp lệ. Cho phép: 1 (Chờ duyệt), 2 (Công khai), 3 (Tạm ẩn), 4 (Bị khóa)");
        }
        
        // Admin chỉ được chuyển đổi giữa status 2, 3, 4
        if (status == 1) {
            throw new IllegalArgumentException("Không thể chuyển homestay về trạng thái 'Chờ duyệt'. Chỉ cho phép: 2 (Công khai), 3 (Tạm ẩn), 4 (Bị khóa)");
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
        
        // Chuyển đổi sang Map
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        
        try {
            // Lấy lịch sử từ database với thông tin user (sử dụng native query có JOIN)
            java.util.List<Object[]> historyResults = 
                    homestayStatusHistoryRepository.findByHomestayIdWithUserInfo(homestayId);
            
            System.out.println("🟡 [History] Found " + historyResults.size() + " history records for homestay " + homestayId);
            
            for (Object[] row : historyResults) {
                java.util.Map<String, Object> item = new java.util.HashMap<>();
                
                // Convert types carefully
                item.put("id", row[0] != null ? ((Number) row[0]).longValue() : null);
                item.put("homestayId", row[1] != null ? ((Number) row[1]).longValue() : null);
                item.put("oldStatus", row[2] != null ? ((Number) row[2]).intValue() : null);
                item.put("newStatus", row[3] != null ? ((Number) row[3]).intValue() : null);
                item.put("reason", row[4]);
                item.put("changedBy", row[5] != null ? ((Number) row[5]).longValue() : null);
                item.put("changedAt", row[6]);
                item.put("changedByName", row[7]);
                item.put("changedByEmail", row[8]);
                
                System.out.println("🟡 [History] Record: " + item);
                result.add(item);
            }
        } catch (Exception e) {
            // Log lỗi nhưng không throw, trả về list rỗng
            System.err.println("🔴 [History] Error fetching homestay status history: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Nếu không có lịch sử, thêm trạng thái hiện tại
        if (result.isEmpty()) {
            java.util.Map<String, Object> currentStatus = new java.util.HashMap<>();
            currentStatus.put("id", null);
            currentStatus.put("homestayId", homestay.getId());
            currentStatus.put("oldStatus", null);
            currentStatus.put("newStatus", homestay.getStatus() != null ? homestay.getStatus().intValue() : null);
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
     * Duyệt yêu cầu cập nhật homestay
     */
    @Transactional
    public void approvePendingUpdate(Long pendingId, Long adminId) {
        hbm.adminservice.entity.HomestayPending pending = homestayPendingRepository.findById(pendingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu cập nhật"));
        
        if (!"waiting".equals(pending.getStatus())) {
            throw new IllegalArgumentException("Yêu cầu này đã được xử lý");
        }
        
        // Cập nhật status thành 'approved'
        pending.setStatus("approved");
        pending.setReviewedBy(adminId);
        pending.setReviewedAt(LocalDateTime.now());
        
        homestayPendingRepository.save(pending);
        
        System.out.println("✅ [Approve] Approved pending request " + pendingId + " by admin " + adminId);
    }
    
    /**
     * Từ chối yêu cầu cập nhật homestay
     */
    @Transactional
    public void rejectPendingUpdate(Long pendingId, Long adminId, String reason) {
        hbm.adminservice.entity.HomestayPending pending = homestayPendingRepository.findById(pendingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu cập nhật"));
        
        if (!"waiting".equals(pending.getStatus())) {
            throw new IllegalArgumentException("Yêu cầu này đã được xử lý");
        }
        
        // Cập nhật status thành 'rejected'
        pending.setStatus("rejected");
        pending.setReviewedBy(adminId);
        pending.setReviewedAt(LocalDateTime.now());
        pending.setReason(reason);
        
        homestayPendingRepository.save(pending);
        
        System.out.println("❌ [Reject] Rejected pending request " + pendingId + " by admin " + adminId + " - Reason: " + reason);
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
        
        // Kiểm tra xem có yêu cầu cập nhật đang chờ duyệt không
        // Logic: Nếu homestay đã được duyệt (status = 2) nhưng có updatedAt > approvedAt
        // thì có nghĩa là chủ nhà đã cập nhật thông tin sau khi được duyệt
        dto.setIsUpdate(checkHasPendingUpdate(homestay));
        
        // Lấy thông tin chủ nhà
        if (homestay.getUserId() != null) {
            userRepository.findById(homestay.getUserId()).ifPresent(user -> {
                HomestayDTO.HostInfo hostInfo = new HomestayDTO.HostInfo();
                hostInfo.setId(user.getId());
                hostInfo.setName(user.getName());
                hostInfo.setEmail(user.getEmail());
                dto.setHost(hostInfo);
            });
        }
        
        // Lấy danh sách ảnh
        try {
            java.util.List<HomestayImage> images = homestayImageRepository
                    .findByHomestayIdOrderByIsPrimaryDesc(homestay.getId());
            
            if (!images.isEmpty()) {
                java.util.List<HomestayImageDTO> imageDTOs = images.stream()
                        .map(img -> {
                            HomestayImageDTO imgDTO = new HomestayImageDTO();
                            imgDTO.setId(img.getId());
                            imgDTO.setUrl(img.getUrl());
                            imgDTO.setAlt(img.getAlt());
                            imgDTO.setIsPrimary(img.getIsPrimary());
                            return imgDTO;
                        })
                        .collect(Collectors.toList());
                dto.setImages(imageDTOs);
                
                System.out.println("🖼️ [Images] Loaded " + imageDTOs.size() + " images for homestay " + homestay.getId());
            } else {
                System.out.println("🖼️ [Images] No images found for homestay " + homestay.getId());
            }
        } catch (Exception e) {
            System.err.println("🔴 [Images] Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dto;
    }
    
    /**
     * Kiểm tra xem homestay có yêu cầu cập nhật đang chờ duyệt không
     * Logic: Nếu homestay đã được duyệt (status = 2) nhưng có updatedAt > approvedAt
     * thì có nghĩa là chủ nhà đã cập nhật thông tin sau khi được duyệt
     */
    private Boolean checkHasPendingUpdate(Homestay homestay) {
        // Chỉ áp dụng cho homestay đã được duyệt (status = 2)
        if (homestay.getStatus() == null || homestay.getStatus() != 2) {
            return false;
        }
        
        // Nếu chưa có approvedAt thì không có pending update
        if (homestay.getApprovedAt() == null) {
            return false;
        }
        
        // Nếu updatedAt > approvedAt thì có pending update
        if (homestay.getUpdatedAt() != null && 
            homestay.getUpdatedAt().isAfter(homestay.getApprovedAt())) {
            return true;
        }
        
        return false;
    }
}
