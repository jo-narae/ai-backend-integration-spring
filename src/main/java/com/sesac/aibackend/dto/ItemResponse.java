package com.sesac.aibackend.dto;

import com.sesac.aibackend.domain.Item;

/**
 * Item 응답 DTO.
 *
 * 엔티티 직접 노출 금지 — 학습자가 동일 패턴을 다른 컨트롤러에도 적용하도록 유도합니다.
 */
public record ItemResponse(Long id, String name, int price) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getPrice());
    }
}
