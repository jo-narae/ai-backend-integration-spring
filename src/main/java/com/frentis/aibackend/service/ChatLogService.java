package com.frentis.aibackend.service;

import com.frentis.aibackend.domain.ChatLog;
import com.frentis.aibackend.domain.User;
import com.frentis.aibackend.error.NotFoundException;
import com.frentis.aibackend.repository.ChatLogRepository;
import com.frentis.aibackend.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅 로그 저장/조회 서비스.
 *
 * 트랜잭션 경계를 서비스 메서드 단위로 명시합니다.
 * 비동기 흐름(Mono)에서 호출되더라도 이 메서드는 동기 트랜잭션에서 실행됩니다.
 */
@Service
@RequiredArgsConstructor
public class ChatLogService {

    private final UserRepository userRepository;
    private final ChatLogRepository chatLogRepository;

    @Transactional
    public ChatLog save(String username, String prompt, String response) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> NotFoundException.of("user", username));
        return chatLogRepository.save(
                ChatLog.builder()
                        .user(user)
                        .prompt(prompt)
                        .response(response)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<ChatLog> findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> NotFoundException.of("user", username));
        return chatLogRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
}
