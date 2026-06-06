package com.sesac.aibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 3.5 진입점.
 *
 * 실행 프로파일:
 * - dev (기본): H2 인메모리, ddl-auto=create-drop
 * - prod      : PostgreSQL, ddl-auto=update
 */
@SpringBootApplication
public class AiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiBackendApplication.class, args);
    }
}
