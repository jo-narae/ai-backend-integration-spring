package com.frentis.aibackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 도메인.
 *
 * Day 3에서 만들어지고 Day 4에서 Spring Security의 인증 주체가 됩니다.
 *
 * 인증 출처를 provider로 구분합니다.
 * - LOCAL: 폼 가입. passwordHash(BCrypt) 보유, providerId 없음
 * - GOOGLE: 소셜 로그인. passwordHash는 NULL, providerId에 OIDC sub(구글의 불변 고유 ID) 저장
 *
 * 소셜 사용자의 신원 키는 이메일이 아니라 (provider, providerId)입니다.
 * 이메일은 변경·재사용·미검증이 가능하므로 1차 식별자로 쓰지 않습니다.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_provider_provider_id",
                columnNames = {"provider", "provider_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    /** BCrypt 해시 — 평문 저장 금지. 소셜 로그인 사용자는 로컬 비밀번호가 없으므로 NULL. */
    @Column(length = 200)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;   // "USER" 또는 "ADMIN"

    /** 인증 출처: "LOCAL"(폼 가입) 또는 "GOOGLE"(소셜 로그인). */
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String provider = "LOCAL";

    /** 소셜 공급자의 안정적 고유 식별자(OIDC sub). LOCAL 사용자는 NULL. */
    @Column(name = "provider_id", length = 255)
    private String providerId;

    /**
     * 소셜(OAuth2) 로그인 사용자 생성 팩토리.
     *
     * 로컬 비밀번호가 없으므로 passwordHash는 NULL로 두고,
     * 구글이 보증하는 불변 식별자(sub)를 providerId에 저장합니다.
     * 이로써 이 계정은 폼 로그인(/login)으로는 인증될 수 없고 구글 로그인만 가능합니다.
     */
    public static User oauthUser(String email, String providerId) {
        return User.builder()
                .username(email)
                .passwordHash(null)
                .role("USER")
                .provider("GOOGLE")
                .providerId(providerId)
                .build();
    }
}
