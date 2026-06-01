package com.frentis.aibackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI / OpenAPI 설정 (Day 5).
 *
 * 전역 SecurityRequirement는 추가하지 않습니다.
 * 보호된 컨트롤러/메서드에 {@code @SecurityRequirement(name="bearerAuth")} 를 부착합니다.
 * 이로써 /login, /signup, /health 는 Swagger UI에서 자물쇠 없이 표시됩니다.
 *
 * /swagger-ui.html 우상단 "Authorize" 버튼으로 JWT 등록 후 보호 라우트를 호출하실 수 있습니다.
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
