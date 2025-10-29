package hbm.adminservice.repository;

import hbm.adminservice.entity.HomestayPending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomestayPendingRepository extends JpaRepository<HomestayPending, Long> {
    
    @Query("SELECT hp FROM HomestayPending hp WHERE hp.status = :status ORDER BY hp.submittedAt ASC")
    List<HomestayPending> findByStatusOrderBySubmittedAtAsc(@Param("status") String status);
}
