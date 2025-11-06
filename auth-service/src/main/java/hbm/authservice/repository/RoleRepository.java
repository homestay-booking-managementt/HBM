package hbm.authservice.repository;

import hbm.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {

    @Query("""
    select r.name
    from Role r
    join UserRole ur on ur.roleId = r.id
    where ur.userId = :userId
    """)
    List<String> findByUserId(@Param("userId") Long userId);

    Optional<Role> findByName(String name);
}
