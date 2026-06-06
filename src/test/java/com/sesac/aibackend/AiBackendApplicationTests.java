package com.sesac.aibackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 컨텍스트 로드 테스트.
 *
 * H2 인메모리 프로파일(dev)로 부팅하여 Bean 구성에 문제가 없는지만 확인합니다.
 */
@SpringBootTest
@ActiveProfiles("dev")
class AiBackendApplicationTests {

    @Test
    void contextLoads() {
        // 컨텍스트가 정상 로드되면 통과
    }
}
