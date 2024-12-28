package com.ktds.krater.query.repository;

import com.ktds.krater.common.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {
   
   @Query("SELECT u FROM Usage u WHERE u.userId = :userId")
   Optional<Usage> findByUserId(String userId);
}
