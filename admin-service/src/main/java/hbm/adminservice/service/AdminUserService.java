package hbm.adminservice.service;

import hbm.adminservice.dto.UserDTO;
import hbm.adminservice.entity.User;
import hbm.adminservice.repository.UserRepository;
import hbm.adminservice.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
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
}
