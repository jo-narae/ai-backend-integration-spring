package com.sesac.aibackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI / OpenAPI 설정.
 *
 * springdoc 의존성(build.gradle)은 Day 2부터 상주하여 /swagger-ui.html 자동 문서가 동작합니다.
 * 이 SwaggerConfig(Bearer 보안 스킴)는 Day 4(SP-14, JWT 검증 필터)에서 추가하여,
 * /swagger-ui.html 우상단 "Authorize" 버튼으로 JWT를 등록한 뒤 보호 라우트를 호출하게 합니다.
 *
 * 전역 SecurityRequirement는 추가하지 않습니다.
 * 보호된 컨트롤러/메서드에 {@code @SecurityRequirement(name="bearerAuth")} 를 부착하는
 * 라우트별 자물쇠 정교화는 Day 5(SP-22)에서 진행합니다.
 * 이로써 /login, /signup, /health 는 Swagger UI에서 자물쇠 없이 표시됩니다.
 */
@Configuration
public class SwaggerConfig {

    public static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Backend Gateway")
                        .version("1.0")
                        .description("Day 2~5 산출물 — Spring Boot 3.5 + Security 6 + JPA + WebClient"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
