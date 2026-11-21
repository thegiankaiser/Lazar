package com.lazar.repository;

import com.lazar.model.DailyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyUsageRepository extends JpaRepository<DailyUsage, Long> {
    Optional<DailyUsage> findByUser_IdAndUsageDate(Long userId, LocalDate usageDate);
}
