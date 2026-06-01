package com.frentis.aibackend.repository;

import com.frentis.aibackend.domain.ChatLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    List<ChatLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
