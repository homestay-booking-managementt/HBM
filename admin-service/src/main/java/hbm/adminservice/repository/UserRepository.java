package hbm.adminservice.repository;

import hbm.adminservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Lấy danh sách user theo role
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN UserRole ur ON u.id = ur.userId " +
           "JOIN Role r ON ur.roleId = r.id " +
           "WHERE UPPER(r.name) = UPPER(:roleName)")
    List<User> findByRole(@Param("roleName") String roleName);
}
