package hbm.adminservice.repository;

import hbm.adminservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    
    /**
     * Lấy danh sách role names của một user
     */
    @Query("SELECT r.name FROM UserRole ur " +
           "JOIN Role r ON ur.roleId = r.id " +
           "WHERE ur.userId = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);
}
