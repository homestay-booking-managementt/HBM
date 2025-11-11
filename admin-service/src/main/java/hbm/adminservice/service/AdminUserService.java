package hbm.adminservice.service;

import hbm.adminservice.dto.UserDTO;
import hbm.adminservice.dto.UserStatusHistoryDTO;
import hbm.adminservice.entity.User;
import hbm.adminservice.repository.UserRepository;
import hbm.adminservice.repository.UserRoleRepository;
import hbm.adminservice.repository.UserStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private UserStatusHistoryRepository userStatusHistoryRepository;
    
    /**
     * Lấy danh sách người dùng theo role
     * @param role Role cần lọc (CUSTOMER, HOST, ADMIN) - nếu null thì lấy tất cả
     * @return Danh sách UserDTO
     */
    public List<UserDTO> getUsersByRole(String role) {
        List<User> users;
        
        if (role != null && !role.trim().isEmpty()) {
            // Lọc theo role
            users = userRepository.findByRole(role.trim());
        } else {
            // Lấy tất cả user
            users = userRepository.findAll();
        }
        
        // Convert sang DTO
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật trạng thái user
     * @param userId ID của user cần cập nhật
     * @param newStatus Trạng thái mới (0:chờ duyệt, 1:hoạt động, 2:tạm khóa, 3:bị chặn)
     * @param reason Lý do thay đổi trạng thái
     * @param adminId ID của admin thực hiện thay đổi
     * @return UserDTO đã được cập nhật
     */
    public UserDTO updateUserStatus(Long userId, Integer newStatus, String reason, Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
        
        // Validate status
        if (newStatus < 0 || newStatus > 3) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ. Phải từ 0-3");
        }
        
        user.setStatus(newStatus);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        
        // Lưu user
        User updatedUser = userRepository.save(user);
        
        // Note: user_status_history sẽ được tự động lưu bởi trigger MySQL
        
        return convertToDTO(updatedUser);
    }
    
    /**
     * Convert User entity sang UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setStatus(user.getStatus());
        dto.setIsDeleted(user.getIsDeleted());
        
        // Lấy danh sách roles của user
        List<String> roles = userRoleRepository.findRoleNamesByUserId(user.getId());
        dto.setRoles(roles);
        
        return dto;
    }
    
    /**
     * Lấy lịch sử thay đổi trạng thái của user
     * @param userId ID của user cần xem lịch sử
     * @return Danh sách UserStatusHistoryDTO
     */
    public List<UserStatusHistoryDTO> getUserStatusHistory(Long userId) {
        List<Object[]> results = userStatusHistoryRepository.findByUserIdWithChangedByInfo(userId);
        List<UserStatusHistoryDTO> historyList = new ArrayList<>();
        
        for (Object[] row : results) {
            UserStatusHistoryDTO dto = new UserStatusHistoryDTO();
            dto.setId(((Number) row[0]).longValue());
            dto.setUserId(((Number) row[1]).longValue());
            dto.setOldStatus(row[2] != null ? ((Number) row[2]).intValue() : null);
            dto.setNewStatus(row[3] != null ? ((Number) row[3]).intValue() : null);
            dto.setReason((String) row[4]);
            dto.setChangedBy(row[5] != null ? ((Number) row[5]).longValue() : null);
            dto.setChangedAt(row[6] != null ? ((Timestamp) row[6]).toLocalDateTime() : null);
            dto.setChangedByName((String) row[7]);
            dto.setChangedByEmail((String) row[8]);
            
            historyList.add(dto);
        }
        
        return historyList;
    }
}
