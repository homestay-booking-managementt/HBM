package hbm.authservice.repository;

import hbm.authservice.dto.UserDto;
import hbm.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
    select u.id, u.name, u.email, u.phone, u.status
    from User u
    """)
    UserDto findUserDtoById(@Param("id") Long id);
}
