# ai-backend-integration-spring (Day 2~5 산출물)

Spring Boot 3.5 / JDK 21 / Spring Security 6 / Hibernate 6 기반 게이트웨이 서버입니다.

## 사전 요구사항

- JDK 21 (Temurin 또는 Corretto) — `build.gradle`의 toolchain이 21로 고정되어 있습니다
- Docker Desktop (PostgreSQL용, Day 3 prod 프로파일에서)
- Python FastAPI 서버 (`ai-backend-integration-python`) — Day 5 통합 시

## 셋업

### 개발 프로파일 (기본값, H2 인메모리 + 시드 데이터)

```bash
./gradlew bootRun
```

부팅 시 `DataInitializer`가 다음 계정을 자동 생성합니다.

| username | password | role |
|----------|----------|------|
| admin | admin1234 | ADMIN |
| alice | password123 | USER (+ 샘플 ChatLog 3건) |

H2 콘솔: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:aibackend`
- User Name: `sa`
- Password: (비움)

### 운영 프로파일 (PostgreSQL)

```bash
cp .env.example .env
# .env 편집 - JWT_SECRET을 반드시 32바이트 이상으로
# 구글 OAuth(Day 4 B7~B8) 사용 시 GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET도 채웁니다

docker compose up -d
./gradlew bootRun --args='--spring.profiles.active=prod'
```

> 구글 OAuth 자격 증명은 구글 클라우드 콘솔에서 발급합니다. 승인된 리디렉션 URI에 `http://localhost:8080/login/oauth2/code/google`을 반드시 등록하십시오. `GOOGLE_CLIENT_ID`/`GOOGLE_CLIENT_SECRET`은 저장소에 커밋하지 마십시오.

> ⚠️ JwtUtil은 부팅 시 secret이 32바이트 미만이면 `IllegalStateException`을 던집니다. dev 기본값은 부팅용일 뿐, 운영에는 반드시 환경변수를 설정하시기 바랍니다.

기동 후:
- Swagger UI: <http://localhost:8080/swagger-ui.html> (보호 라우트만 자물쇠 표시)
- 헬스 체크: <http://localhost:8080/health>

### IntelliJ에서 실행

IntelliJ로 직접 기동하려면 두 가지를 맞춰야 합니다.

**1. JDK 21로 통일** (이게 안 맞으면 `UnsupportedClassVersionError: class file version 65.0` 발생 — 21로 컴파일된 클래스를 17 런타임이 못 읽는 경우)

- `File → Project Structure → Project`: **SDK = 21**, **Language level = 21**
- `Settings → Build, Execution, Deployment → Build Tools → Gradle`: **Gradle JVM = 21**
- 실행 구성(`Edit Configurations...`)의 **JRE = 21 또는 Project SDK**
- 변경 후 `Build → Rebuild Project`

**2. 환경변수 주입** — `gradlew bootRun`은 `build.gradle`이 `.env`를 읽어 주입하지만, **IntelliJ의 Application 실행 구성은 `.env`를 자동으로 읽지 않습니다.** 둘 중 하나로 주입하십시오.

- (간단) 실행 구성의 **Environment variables**에 직접 입력:
  `POSTGRES_PASSWORD`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `OAUTH2_REDIRECT_URI`, `PYTHON_BASE_URL`
- (권장) **EnvFile 플러그인** 설치 후 실행 구성에서 `.env` 파일을 지정 (`gradlew bootRun`과 동일한 값을 그대로 사용)

> prod 프로파일(PostgreSQL)로 띄우려면 실행 구성의 환경변수에 `SPRING_PROFILES_ACTIVE=prod`를 추가하거나, VM/program 인자로 `--spring.profiles.active=prod`를 넘기십시오. 기본값도 `prod`입니다.

## 엔드포인트

| 메서드 | 경로 | 인증 | 설명 | 사용 위치 |
|--------|------|------|------|----------|
| GET | `/health` | X | 헬스 체크 | Day 2 |
| GET/POST/PUT/DELETE | `/legacy/items/**` | X | **인메모리 CRUD** (Day 2 학습용) | Day 2 |
| GET/POST/PUT/DELETE | `/items/**` | ✅ JWT | **JPA CRUD** (Day 3에서 등장) | Day 3+ |
| POST | `/signup` | X | 회원가입 | Day 4 |
| POST | `/login` | X | 로그인 + JWT 발급 | Day 4 |
| GET | `/oauth2/authorization/google` | X | 구글 OAuth 로그인 시작 (Spring 자동) | Day 4 |
| GET | `/login/oauth2/code/google` | X | 구글 OAuth 콜백 (Spring 자동, 성공 시 앱 JWT 발급) | Day 4 |
| POST | `/chat` | ✅ JWT | FastAPI 프록시 + ChatLog 저장 | Day 5 |
| GET | `/chat/logs` | ✅ JWT | 내 채팅 이력 | Day 5 |
| GET | `/admin/users` | ✅ ROLE_ADMIN | 관리자 전용 | Day 4 |

## 빠른 사용 예시

```bash
# 1) 로그인 (시드 계정 사용)
TOKEN=$(curl -sX POST http://localhost:8080/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"password123"}' | jq -r .token)

# 2) 채팅 (Python FastAPI가 켜져 있어야 함)
curl -X POST http://localhost:8080/chat \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"prompt":"FastAPI를 한 줄로"}'

# 3) 내 이력 조회
curl http://localhost:8080/chat/logs \
  -H "Authorization: Bearer $TOKEN"

# 4) 관리자 권한 시연
ADMIN_TOKEN=$(curl -sX POST http://localhost:8080/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin1234"}' | jq -r .token)

curl http://localhost:8080/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

## 디렉터리 구조

```
src/main/java/com/sesac/aibackend/
├── AiBackendApplication.java   진입점
├── domain/                     User, ChatLog, Item (Day 3)
├── repository/                 JpaRepository 3개
├── dto/                        요청·응답 record
│   ├── ChatRequest, ChatResponse
│   ├── ChatLogResponse         ★ 추가 (Day 5 이력 조회용)
│   ├── ItemRequest, ItemResponse
│   └── LoginRequest, SignupRequest
├── service/
│   ├── ChatLogService          저장/조회 트랜잭션 경계
│   └── PythonChatClient        WebClient 호출 (Day 5)
├── security/                   ★ Day 4 보일러플레이트 (사전 배포 권장)
│   ├── SecurityConfig          SecurityFilterChain Bean 방식 (Security 6) + oauth2Login
│   ├── JwtUtil                 JJWT 0.12 + 키 길이 검증
│   ├── JwtAuthenticationFilter UserDetails principal 세팅
│   ├── UserDetailsServiceImpl
│   ├── RestAuthenticationEntryPoint  ★ 추가 (401 표준 응답)
│   └── OAuth2LoginSuccessHandler     ★ 추가 (Day 4 B8, OIDC → 앱 JWT 발급)
├── controller/
│   ├── HealthController
│   ├── Day2ItemController      ★ 추가 (인메모리, /legacy/items)
│   ├── ItemController          JPA, /items (Day 3+)
│   ├── AuthController          /signup, /login
│   ├── ChatController          /chat, /chat/logs (Day 5)
│   └── AdminController         /admin/users
├── config/
│   ├── WebClientConfig         Day 5
│   ├── CorsConfig              Day 5 (헤더 화이트리스트 명시)
│   ├── SwaggerConfig           Day 4 SP-14 (Bearer 스킴 → Authorize). springdoc 의존성은 Day 2부터. 라우트별 @SecurityRequirement는 Day 5
│   └── DataInitializer         ★ 추가 (dev 프로파일 시드)
└── error/
    ├── GlobalExceptionHandler  400/401/403/404/409/500 매핑
    ├── ErrorResponse
    ├── NotFoundException       ★ 추가
    └── DuplicateException      ★ 추가
```

## 강의 매핑

| 블록 | 학습 내용 | 핵심 파일 |
|------|----------|----------|
| Day 2 B1~B4 | Spring Boot 기초 + Bean + @RestController | `AiBackendApplication`, `HealthController` |
| Day 2 B5~B7 | REST 5메서드 + **인메모리 CRUD** + 예외 처리 | `controller/Day2ItemController` (`/legacy/items`), `error/*` |
| Day 3 B1~B4 | JDBC→JPA + PostgreSQL + 엔티티 + JpaRepository | `domain/*`, `repository/*`, `application.yml` |
| Day 3 B5~B7 | 연관관계 + ChatLog 도메인 + 인메모리→JPA 교체 | `User`, `ChatLog`, `ChatLogService`, `controller/ItemController` (`/items`) |
| Day 4 B1~B4 | Security 6 + UserDetailsService + JWT 발급 | `security/SecurityConfig`, `security/JwtUtil`, `AuthController` |
| Day 4 B5~B6 | JWT 검증 필터 + 역할 권한 + Swagger Authorize(Bearer 스킴) | `security/JwtAuthenticationFilter`, `AdminController`, `config/SwaggerConfig` |
| Day 4 B7~B8 | 구글 OAuth2 Client 구성 + 성공 핸들러로 앱 JWT 발급 | `application.yml`(oauth2 client), `security/SecurityConfig`(oauth2Login), `security/OAuth2LoginSuccessHandler` |
| Day 5 B1~B3 | WebClient + /chat 프록시 + ChatLog 저장 | `config/WebClientConfig`, `service/PythonChatClient`, `controller/ChatController`, `service/ChatLogService` |
| Day 5 B4~B7 | CORS + Swagger 라우트별 자물쇠(@SecurityRequirement) + 환경변수 + React 시연 | `config/CorsConfig`, 세 컨트롤러(@SecurityRequirement), `dto/ChatLogResponse` (이력 화면) |

## 강사 진행 가이드

### Day 2 ↔ Day 3 흐름 — URL 분리로 자연스럽게 교체

- **Day 2 B5~B7**: `Day2ItemController` 만 다룹니다. URL은 `/legacy/items`. JPA 단어를 꺼내지 않습니다.
- **Day 3 첫 블록**: "어제 만든 인메모리 컨트롤러를 영속화하면 어떻게 될까요?"로 시작 → `ItemController`(`/items`)로 자연 전환.
- 두 컨트롤러가 공존하지만 URL이 다르므로 충돌이 없습니다.

### Day 4 Security — 사전 배포 권장

`security/` 패키지 전체와 `application.yml`의 `jwt.*` 키를 git checkout으로 학생에게 즉시 받게 한 뒤, 라이브에서는 다음 순서로 설명하시기 바랍니다.

1. **B1**: `SecurityFilterChain` 람다 DSL 한 줄씩 의미 설명
2. **B2**: `UserDetailsServiceImpl` 작성을 라이브로 (학생도 따라 작성)
3. **B3**: jwt.io 사이트로 토큰 디코딩 시연
4. **B4**: `AuthController` `/login` 라이브 작성 (JwtUtil은 보일러플레이트 그대로)
5. **B5~B6**: `JwtAuthenticationFilter`는 보일러플레이트 그대로 보여주고 핵심만 강조
6. **B7**: 구글 콘솔에서 동의 화면·OAuth 클라이언트 ID 발급을 라이브로. 승인된 리디렉션 URI는 강사 화면을 학생이 그대로 복사(오타 1글자가 `redirect_uri_mismatch`). 이어서 `application.yml`의 `registration.google` 작성과 `SecurityConfig`의 `.oauth2Login()` 한 줄 추가
7. **B8**: `OAuth2LoginSuccessHandler`(보일러플레이트)로 "구글 신원 확인 → 앱 JWT 발급" 다리 개념 설명 후, 브라우저로 `/oauth2/authorization/google` 접속해 실제 로그인 → 토큰 발급 리다이렉트 시연. SPA가 없으면 토큰을 JSON으로 내려주는 단순화 버전 사용 가능

### Day 5 B1~B3 — 가장 빡빡한 구간

진행 순서 권장:

1. **시작 전 체크**: Python FastAPI가 8000 포트에서 동작 중인지 `curl http://localhost:8000/health` 확인
2. **B1**: `WebClientConfig` 보일러플레이트 보여주기 (라이브 작성 X)
3. **B2**: `PythonChatClient` 작성 → `ChatController` `/chat` 라이브 작성
4. **B3**: ChatLog 저장 통합 → `dto/ChatLogResponse` + `GET /chat/logs` 추가 시연

### 시드 데이터 활용

`DataInitializer`가 admin/alice + 샘플 ChatLog 3건을 자동 생성하므로 다음 시연이 즉시 가능합니다.

- Day 4 B6 관리자 권한 시연 (`admin/admin1234`로 로그인 후 `/admin/users`)
- Day 5 B4 React 시연 (`alice/password123`로 로그인 후 이력 3건 표시)

## Spring Security 6 변경사항 (OFF-JT 30기 자료와 다름)

- `WebSecurityConfigurerAdapter` 제거 → `SecurityFilterChain` Bean
- `authorizeRequests` → `authorizeHttpRequests`
- `antMatchers` → `requestMatchers`
- `.and()` 체이닝 → 람다 DSL (`AbstractHttpConfigurer::disable` 메서드 참조 권장)
- `@EnableGlobalMethodSecurity` → `@EnableMethodSecurity`
- `AuthenticationManager` 자동 노출 X → Bean 명시 구성

`references/migration-2-to-3.md` 참조.

## 예외 매핑 표준

| HTTP | 코드 | 예외 |
|------|------|------|
| 400 | `VALIDATION_FAILED` | `MethodArgumentNotValidException` |
| 400 | `BAD_REQUEST` | `IllegalArgumentException` |
| 401 | `UNAUTHORIZED` | `AuthenticationException`, JWT 검증 실패 (`RestAuthenticationEntryPoint`) |
| 403 | `FORBIDDEN` | `AccessDeniedException` |
| 404 | `NOT_FOUND` | `NotFoundException` |
| 409 | `CONFLICT` | `DuplicateException` |
| 500 | `INTERNAL` | 그 외 모든 예외 |

응답 본문 예:

```json
{
  "code": "NOT_FOUND",
  "message": "item not found: 99",
  "timestamp": "2026-05-29T08:00:00Z"
}
```

## 테스트

```bash
./gradlew test
```

`AiBackendApplicationTests`는 컨텍스트 로드만 검증합니다.

### API 수동 테스트 (curl과 Swagger)

springdoc 자동 문서는 **Day 2부터** 동작하므로, 굳이 Postman/Insomnia를 설치하지 않아도 됩니다.

- **curl**: 원리 확인용. 위 예시처럼 주소·헤더·본문을 직접 적습니다.
- **Swagger UI** (`/swagger-ui.html`): 브라우저에서 값을 채워 **Try it out**으로 호출. `@RequestBody`가 있는 POST는 따옴표 이스케이프 없이 편합니다.
- **JWT 보호 라우트**: **Day 4(SP-14)**에서 `SwaggerConfig`(Bearer 스킴)를 등록하면 우상단 **Authorize** 버튼이 생깁니다. `/login`으로 받은 토큰을 붙이면 이후 호출에 `Authorization: Bearer`가 자동 부착됩니다.

> 교육 흐름 권장: 토큰을 헤더에 싣는 원리는 curl로 한 번만 보여 주고, 반복 호출은 Swagger Authorize로 전환합니다.

## 트러블슈팅

| 증상 | 원인 | 해결 |
|------|------|------|
| `UnsupportedClassVersionError: class file version 65.0` | 21로 컴파일됐는데 IntelliJ 실행 SDK가 17 (65.0=Java21, 61.0=Java17) | Project SDK·Gradle JVM·실행 구성 JRE를 모두 **21**로 (위 "IntelliJ에서 실행" 참조) |
| 구글 로그인이 IntelliJ 실행 시 `invalid_client` | IntelliJ가 `.env`를 안 읽어 `GOOGLE_CLIENT_*` 미주입 | 실행 구성 환경변수에 직접 입력하거나 EnvFile 플러그인 사용 |
| `IllegalStateException: jwt.secret must be at least 32 bytes` | JWT_SECRET 길이 부족 | `.env`의 `JWT_SECRET`을 32자 이상으로 |
| `/chat`이 502/타임아웃 | Python FastAPI 미기동 | `uvicorn app.main:app` 확인 |
| 401 Unauthorized (JSON) | JWT 누락 또는 만료 | `Authorization: Bearer <token>` 확인 |
| 401 Unauthorized (HTML) | `RestAuthenticationEntryPoint` 미등록 | SecurityConfig `.exceptionHandling` 확인 |
| CORS 차단 | 허용 오리진 미설정 | `config/CorsConfig`의 `setAllowedOrigins` |
| PostgreSQL 연결 실패 | Docker 미기동 | `docker compose up -d` |
| `/admin/**` 403 | ROLE_USER로 접근 | `admin/admin1234`로 로그인 |
| 시드 데이터 안 보임 | prod 프로파일로 기동 | `DataInitializer`는 `@Profile("dev")` 한정 |
| 구글 로그인 `redirect_uri_mismatch` | 콘솔 등록 URI와 실제 콜백 불일치 | 콘솔에 `http://localhost:8080/login/oauth2/code/google` 정확히 등록 |
| 구글 로그인 후 401/무한 리다이렉트 | `OAuth2LoginSuccessHandler` 미등록 또는 JWT 미발급 | `SecurityConfig`의 `.oauth2Login().successHandler(...)` 연결 확인 |
| `invalid_client` | client-id/secret 오타 또는 환경변수 미주입 | `GOOGLE_CLIENT_ID`/`GOOGLE_CLIENT_SECRET` 확인 |
