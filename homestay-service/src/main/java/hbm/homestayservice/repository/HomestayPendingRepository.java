package hbm.homestayservice.repository;

import hbm.homestayservice.entity.HomestayPending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomestayPendingRepository extends JpaRepository<HomestayPending, Long> {
    
    @Query("SELECT hp FROM HomestayPending hp WHERE hp.homestayId = :homestayId ORDER BY hp.submittedAt DESC")
    List<HomestayPending> findByHomestayIdOrderBySubmittedAtDesc(@Param("homestayId") Long homestayId);
    
    @Query("SELECT hp FROM HomestayPending hp WHERE hp.status = :status ORDER BY hp.submittedAt ASC")
    List<HomestayPending> findByStatusOrderBySubmittedAtAsc(@Param("status") String status);
}
