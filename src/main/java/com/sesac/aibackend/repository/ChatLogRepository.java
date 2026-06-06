package com.sesac.aibackend.repository;

import com.sesac.aibackend.domain.ChatLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    List<ChatLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
