package hbm.adminservice.service;

import hbm.adminservice.dto.HomestayPendingDTO;
import hbm.adminservice.entity.HomestayPending;
import hbm.adminservice.repository.HomestayPendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPendingService {
    
    @Autowired
    private HomestayPendingRepository pendingRepository;
    
    /**
     * Lấy danh sách các yêu cầu chờ duyệt
     */
    public List<HomestayPendingDTO> getWaitingRequests() {
        List<HomestayPending> pendings = pendingRepository.findByStatusOrderBySubmittedAtAsc("waiting");
        return pendings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Admin duyệt yêu cầu cập nhật homestay
     */
    @Transactional
    public HomestayPendingDTO approveRequest(Long pendingId, Long adminId) {
        // Validate
        if (pendingId == null || pendingId <= 0) {
            throw new IllegalArgumentException("ID yêu cầu không hợp lệ");
        }
        
        if (adminId == null || adminId <= 0) {
            throw new IllegalArgumentException("ID admin không hợp lệ");
        }
        
        // Tìm pending request
        HomestayPending pending = pendingRepository.findById(pendingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu với ID: " + pendingId));
        
        // Kiểm tra trạng thái
        if (!"waiting".equals(pending.getStatus())) {
            throw new IllegalArgumentException("Yêu cầu này đã được xử lý rồi");
        }
        
        // Cập nhật trạng thái
        pending.setStatus("approved");
        pending.setReviewedBy(adminId);
        pending.setReviewedAt(LocalDateTime.now());
        
        HomestayPending saved = pendingRepository.save(pending);
        
        // Trigger sẽ tự động cập nhật homestay (xem trong homstay.sql)
        
        return convertToDTO(saved);
    }
    
    /**
     * Admin từ chối yêu cầu cập nhật homestay
     */
    @Transactional
    public HomestayPendingDTO rejectRequest(Long pendingId, Long adminId, String reason) {
        // Validate
        if (pendingId == null || pendingId <= 0) {
            throw new IllegalArgumentException("ID yêu cầu không hợp lệ");
        }
        
        if (adminId == null || adminId <= 0) {
            throw new IllegalArgumentException("ID admin không hợp lệ");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập lý do từ chối");
        }
        
        // Tìm pending request
        HomestayPending pending = pendingRepository.findById(pendingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu với ID: " + pendingId));
        
        // Kiểm tra trạng thái
        if (!"waiting".equals(pending.getStatus())) {
            throw new IllegalArgumentException("Yêu cầu này đã được xử lý rồi");
        }
        
        // Cập nhật trạng thái
        pending.setStatus("rejected");
        pending.setReviewedBy(adminId);
        pending.setReviewedAt(LocalDateTime.now());
        pending.setReason(reason);
        
        HomestayPending saved = pendingRepository.save(pending);
        
        // Trigger sẽ tự động gửi thông báo cho host (xem trong homstay.sql)
        
        return convertToDTO(saved);
    }
    
    /**
     * Chuyển đổi entity sang DTO
     */
    private HomestayPendingDTO convertToDTO(HomestayPending pending) {
        HomestayPendingDTO dto = new HomestayPendingDTO();
        dto.setId(pending.getId());
        dto.setHomestayId(pending.getHomestayId());
        dto.setPendingData(pending.getPendingData());
        dto.setSubmittedAt(pending.getSubmittedAt());
        dto.setStatus(pending.getStatus());
        dto.setReviewedBy(pending.getReviewedBy());
        dto.setReviewedAt(pending.getReviewedAt());
        dto.setReason(pending.getReason());
        return dto;
    }
}
