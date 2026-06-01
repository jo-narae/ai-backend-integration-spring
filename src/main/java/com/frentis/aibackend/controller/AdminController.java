package com.frentis.aibackend.controller;

import com.frentis.aibackend.domain.User;
import com.frentis.aibackend.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 전용 라우트 (Day 4 B6 시연).
 *
 * SecurityConfig의 requestMatchers + 메서드 단의 @PreAuthorize 양쪽으로 보호합니다.
 *
 * 시드 계정: admin / admin1234 (DataInitializer, dev 프로파일 한정)
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<Map<String, Object>> listUsers() {
        return userRepository.findAll().stream()
                .map(this::summarize)
                .toList();
    }

    private Map<String, Object> summarize(User u) {
        return Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "role", u.getRole()
        );
    }
}
