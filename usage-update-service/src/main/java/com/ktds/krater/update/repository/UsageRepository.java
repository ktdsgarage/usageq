package com.ktds.krater.update.repository;

import com.ktds.krater.common.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usage u WHERE u.userId = :userId")
    Optional<Usage> findByUserIdWithLock(String userId);
}
