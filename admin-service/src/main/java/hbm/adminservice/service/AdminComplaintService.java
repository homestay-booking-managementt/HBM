package hbm.adminservice.service;

import hbm.adminservice.dto.ComplaintDTO;
import hbm.adminservice.entity.Complaint;
import hbm.adminservice.entity.Homestay;
import hbm.adminservice.entity.User;
import hbm.adminservice.repository.ComplaintRepository;
import hbm.adminservice.repository.HomestayRepository;
import hbm.adminservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminComplaintService {
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HomestayRepository homestayRepository;
    
    /**
     * Lấy danh sách complaint với filter
     */
    public List<ComplaintDTO> getComplaints(String status, Long userId, Long adminId, Boolean unassigned) {
        List<Complaint> complaints;
        
        if (Boolean.TRUE.equals(unassigned)) {
            // Lấy complaint chưa được giao
            complaints = complaintRepository.findUnassignedComplaints();
        } else if (status != null && !status.trim().isEmpty()) {
            // Lọc theo status
            complaints = complaintRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (userId != null) {
            // Lọc theo user
            complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (adminId != null) {
            // Lọc theo admin được giao
            complaints = complaintRepository.findByAssignedAdminIdOrderByCreatedAtDesc(adminId);
        } else {
            // Lấy tất cả
            complaints = complaintRepository.findAllOrderByCreatedAtDesc();
        }
        
        return complaints.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy chi tiết complaint
     */
    public ComplaintDTO getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại với ID: " + id));
        
        return convertToDTO(complaint);
    }
    
    /**
     * Giao complaint cho admin
     */
    @Transactional
    public ComplaintDTO assignComplaint(Long complaintId, Long adminId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại với ID: " + complaintId));
        
        // Validate admin exists
        if (adminId != null) {
            userRepository.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy admin với ID: " + adminId));
        }
        
        complaint.setAssignedAdminId(adminId);
        
        // Nếu đang pending, chuyển sang in_progress
        if ("pending".equals(complaint.getStatus())) {
            complaint.setStatus("in_progress");
        }
        
        Complaint updated = complaintRepository.save(complaint);
        return convertToDTO(updated);
    }
    
    /**
     * Admin phản hồi complaint
     */
    @Transactional
    public ComplaintDTO respondToComplaint(Long complaintId, Long adminId, String status, String response) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại với ID: " + complaintId));
        
        // Validate status
        if (status != null && !status.trim().isEmpty()) {
            complaint.setStatus(status);
        }
        
        // Set admin response
        if (response != null && !response.trim().isEmpty()) {
            complaint.setAdminResponse(response);
        }
        
        // Set assigned admin nếu chưa có
        if (complaint.getAssignedAdminId() == null && adminId != null) {
            complaint.setAssignedAdminId(adminId);
        }
        
        Complaint updated = complaintRepository.save(complaint);
        return convertToDTO(updated);
    }
    
    /**
     * Cập nhật status complaint
     */
    @Transactional
    public ComplaintDTO updateComplaintStatus(Long complaintId, String newStatus) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại với ID: " + complaintId));
        
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status không được để trống");
        }
        
        complaint.setStatus(newStatus);
        Complaint updated = complaintRepository.save(complaint);
        
        return convertToDTO(updated);
    }
    
    /**
     * Convert entity sang DTO
     */
    private ComplaintDTO convertToDTO(Complaint complaint) {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(complaint.getId());
        dto.setUserId(complaint.getUserId());
        dto.setBookingId(complaint.getBookingId());
        dto.setHomestayId(complaint.getHomestayId());
        dto.setSubject(complaint.getSubject());
        dto.setContent(complaint.getContent());
        dto.setStatus(complaint.getStatus());
        dto.setAssignedAdminId(complaint.getAssignedAdminId());
        dto.setAdminResponse(complaint.getAdminResponse());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());
        
        // Lấy thông tin user
        Optional<User> userOpt = userRepository.findById(complaint.getUserId());
        userOpt.ifPresent(user -> {
            dto.setUserName(user.getName());
            dto.setUserEmail(user.getEmail());
            dto.setUserPhone(user.getPhone());
        });
        
        // Lấy thông tin homestay nếu có
        if (complaint.getHomestayId() != null) {
            Optional<Homestay> homestayOpt = homestayRepository.findById(complaint.getHomestayId());
            homestayOpt.ifPresent(homestay -> {
                dto.setHomestayName(homestay.getName());
            });
        }
        
        // Lấy tên admin được giao
        if (complaint.getAssignedAdminId() != null) {
            Optional<User> adminOpt = userRepository.findById(complaint.getAssignedAdminId());
            adminOpt.ifPresent(admin -> {
                dto.setAssignedAdminName(admin.getName());
            });
        }
        
        return dto;
    }
}
