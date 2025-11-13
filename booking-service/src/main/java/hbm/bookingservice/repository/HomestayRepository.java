package hbm.bookingservice.repository;

import hbm.bookingservice.entity.Homestay;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM Homestay h WHERE h.id = :id")
    Optional<Homestay> findByIdWithLock(@Param("id") Long id);
}
