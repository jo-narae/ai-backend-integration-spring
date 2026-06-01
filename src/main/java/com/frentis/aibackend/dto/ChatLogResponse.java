package com.frentis.aibackend.dto;

import com.frentis.aibackend.domain.ChatLog;
import java.time.LocalDateTime;

/**
 * 채팅 이력 응답 DTO.
 *
 * Day 5 React 시연에서 "내 대화 이력" 화면 데이터로 사용됩니다.
 * User 객체를 직접 노출하지 않고 username만 평탄화합니다.
 */
public record ChatLogResponse(
        Long id,
        String prompt,
        String response,
        LocalDateTime createdAt
) {
    public static ChatLogResponse from(ChatLog log) {
        return new ChatLogResponse(
                log.getId(),
                log.getPrompt(),
                log.getResponse(),
                log.getCreatedAt()
        );
    }
}
