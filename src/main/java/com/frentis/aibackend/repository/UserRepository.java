package com.frentis.aibackend.repository;

import com.frentis.aibackend.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    /** 소셜 로그인 신원 조회 — (provider, providerId) 조합이 사용자의 안정적 식별 키입니다. */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
