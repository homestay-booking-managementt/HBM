package hbm.authservice.repository;

import hbm.authservice.entity.UserRole;
import hbm.authservice.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
