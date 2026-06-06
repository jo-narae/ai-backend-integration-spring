package com.sesac.aibackend.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Day 2~3 실습용 도메인.
 *
 * Day 2: 인메모리 {@code ItemRepository}와 함께 CRUD 학습
 * Day 3: 동일 엔티티를 JPA로 영속화
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;
}
