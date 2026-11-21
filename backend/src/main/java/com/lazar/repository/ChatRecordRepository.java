package com.lazar.repository;

import com.lazar.model.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long> {
    List<ChatRecord> findTop50ByUser_IdOrderByCreatedAtDesc(Long userId);
}
