# Study Tracker API — 커스텀 JWT 인증, Redis 원자적 멱등성 제어 및 캐시 정합성 백엔드

외부 JWT 라이브러리에 의존하지 않는 **HMAC-SHA256 인증(상수 시간 서명 검증)**, **Redis `SETNX` 기반 중복 생성 방지(Idempotency)**, **사용자 단위로 격리된 통계 캐싱 및 즉시 무효화**, 그리고 **12개 테스트 클래스(60건의 단위 및 통합 테스트)** 검증에 초점을 맞춰 설계한 **Java 21 / Spring Boot 4** 백엔드 API 서버입니다.

## 배포 및 운영 확인

- **API Server**: https://study-tracker-api-7stm.onrender.com
- **Swagger UI (OpenAPI 3)**: https://study-tracker-api-7stm.onrender.com/swagger-ui/index.html
- **Actuator Health Check**: https://study-tracker-api-7stm.onrender.com/actuator/health

> Render Free 인스턴스 특성상 유휴 상태 이후 첫 요청 시 콜드 스타트 지연이 발생할 수 있습니다.

---

## 핵심 엔지니어링 설계 및 구현 포인트

### 1. 외부 라이브러리 없는 HMAC-SHA256 JWT 직접 구현 및 타이밍 공격 방어
- **구현 위치**: `auth/jwt/JwtTokenProvider.java`, `auth/security/JwtAuthenticationFilter.java`
- `jjwt` 등의 서드파티 라이브러리 없이 표준 JDK `javax.crypto.Mac`(`HmacSHA256`)과 Base64URL 인코딩만으로 토큰 발급, 페이로드 파싱, 만료(`exp`) 검증을 직접 구현했습니다.
- 서명 일치 여부 비교 시 일반 문자열 비교(`equals`) 대신 **`MessageDigest.isEqual` 상수 시간(Constant-Time) 비교**를 적용해 서명 추측 타이밍 공격을 방어했습니다.
- Access Token 만료 시 DB에 관리되는 Refresh Token으로 재발급하며, 로그아웃 시 Refresh Token을 즉시 삭제해 재사용을 차단합니다.

### 2. Redis 원자적 잠금(`SETNX`) 기반 멱등성 제어 (Idempotency-Key)
- **구현 위치**: `global/idempotency/IdempotencyService.java`, `RedisIdempotencyStore.java`, `InMemoryIdempotencyStore.java`
- 네트워크 재시도나 클라이언트 중복 클릭으로 인해 동일 리소스가 중복 생성되는 문제를 막기 위해 `POST /api/todos`에서 `Idempotency-Key` 헤더를 처리합니다.
- 키 형식을 `idempotency:{action}:{userId}:{idempotencyKey}`로 구성해 **사용자 및 액션별로 키 공간을 격리**(타 사용자의 동일 키와 충돌 방지)했습니다.
- Redis `setIfAbsent(key, "PROCESSING", 5분 TTL)` 원자적 연산으로 선점에 실패한 동시/중복 요청은 즉시 `409 Conflict` (`DUPLICATE_REQUEST`)로 차단하고, **하위 비즈니스 로직에서 런타임 예외 발생 시 `delete(key)`로 락을 즉시 해제**해 클라이언트의 정상 재시도가 막히지 않도록 설계했습니다.
- `IdempotencyStore` 인터페이스로 저장소를 추상화해 운영 프로필(`!test`)은 Redis를, 테스트 프로필(`test`)은 인메모리 구현체를 주입받도록 분리했습니다.

### 3. 리소스 소유권 격리 및 쿼리 / 캐시 정량 성능 최적화
- **구현 위치**: `todo/domain/Todo.java`, `todo/service/TodoService.java`, `user/repository/UserRepository.java`
- 단건 조회, 수정, 완료 상태 변경, 삭제 등 모든 상태 접근 메서드에서 단순 `findById` 대신 **`findByIdAndUserId(id, userId)`를 강제**해 인증된 사용자라도 타인의 리소스에 접근할 수 없도록 도메인 계층에서 차단합니다.
- 사용자별 목록/통계 조회 패턴에 맞춰 `idx_todos_user_id_id(user_id, id)` 및 `idx_todos_user_id_completed_id(user_id, completed, id)` **복합 인덱스**를 설계했습니다.
- `User -> Todo` 지연 로딩(Lazy Loading) 구간에 **`LEFT JOIN FETCH`(`findAllWithTodos`)** 를 적용해, 사용자 50명(Todo 250건) 요약 조회 시 Hibernate `Statistics` 기준 **SQL 실행 횟수를 51회(`1 + N`)에서 1회로 98.0% 절감(조회 시간 `16.45ms → 6.80ms`, 58.7% 단축)** 했습니다.
- 반복 호출 빈도가 높은 `GET /api/todos/stats`는 `@Cacheable(value = "todoStats", key = "#userId")` (TTL 60초)를 적용해, Todo 5,000건 기준 100회 반복 조회 시 **DB `COUNT` 쿼리를 200회에서 2회로 99.0% 절감(캐시 히트 시 DB 쿼리 0회, 응답 `9.33ms → 평균 0.02ms`)** 했으며, 생성/수정/완료/완료취소/삭제 5개 변경 경로마다 `@CacheEvict(value = "todoStats", key = "#userId")`로 즉시 무효화해 Stale 데이터 노출을 방지합니다.

| 최적화 항목 | 측정 조건 (`Hibernate Statistics` 통합 테스트) | 적용 전 | 적용 후 | 개선율 |
| --- | --- | --- | --- | --- |
| **JPA `LEFT JOIN FETCH`** (`UserQueryServiceTest`) | 사용자 50명 / Todo 250건 요약 조회 | SQL **51회** (`16.45ms`) | SQL **1회** (`6.80ms`) | **쿼리 98.0% 감소 / 시간 58.7% 단축** |
| **통계 캐시(`@Cacheable`) + 복합 인덱스** (`TodoStatsCacheTest`) | Todo 5,000건 / 동일 사용자 100회 반복 조회 | DB `COUNT` **200회** (`9.33ms`/회) | DB `COUNT` **2회** (캐시 히트 평균 `0.02ms`) | **DB 쿼리 99.0% 감소 (히트 시 0회)** |

### 4. 12개 테스트 클래스 / 60건 단위 및 통합 테스트 + CI 파이프라인
- **구현 위치**: `src/test/java/com/jobprep/studytracker/**`
- `JwtTokenProviderTest`(1바이트 서명/페이로드 위변조, 만료, 파싱 검증 6건), `AuthControllerTest` & `AuthServiceTest`(토큰 발급/갱신/폐기 10건), `TodoControllerTest` & `TodoServiceTest` & `SpringDataTodoJpaRepositoryTest`(소유권 격리, 복합 조건 필터, 멱등성 `409` 검증 27건), `TodoStatsCacheTest`(5개 변경 경로 즉시 무효화 및 5,000건 반복 조회 쿼리 감소 검증 7건), `UserQueryServiceTest`(`LEFT JOIN FETCH` N+1 쿼리 카운트 검증 3건), `UserControllerTest` & `UserServiceTest` & `ActuatorSecurityTest` & `StudyTrackerApiApplicationTests`(7건) 등 **총 12개 테스트 클래스, 60건의 자동화 테스트**를 구축했습니다.
- `main` 브랜치 푸시 시 GitHub Actions에서 `./gradlew clean test` 및 `./gradlew bootJar` 검증 통과 후 Render로 배포됩니다.

---

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| **Language / Runtime** | Java 21 |
| **Framework** | Spring Boot 4.0.6, Spring WebMVC, Spring Validation |
| **Security** | Spring Security, Custom HMAC-SHA256 JWT, BCrypt |
| **Persistence** | Spring Data JPA, Hibernate, PostgreSQL (운영/로컬), H2 (테스트 격리) |
| **Cache & Concurrency** | Redis (`Spring Data Redis`, Spring Cache Abstraction) |
| **Observability & Docs** | Spring Boot Actuator, springdoc-openapi (Swagger UI 3.0) |
| **Testing** | JUnit 5, MockMvc, Mockito (12개 테스트 클래스 / 60건) |
| **CI/CD & Infra** | Docker, Docker Compose, GitHub Actions, Render |

---

## 시스템 아키텍처 및 요청 흐름

```text
[Client]
   │  Authorization: Bearer <ACCESS_TOKEN>
   │  Idempotency-Key: <UNIQUE_KEY> (POST /api/todos)
   ▼
[JwtAuthenticationFilter] ──▶ JwtTokenProvider (HMAC-SHA256 + 상수 시간 서명 검증)
   ▼
[TodoController / AuthController / UserController]
   ├── POST /api/todos ──▶ IdempotencyService ──▶ Redis SETNX (중복 시 409 Conflict / 예외 시 락 해제)
   ├── GET  /api/todos/stats ──▶ @Cacheable(todoStats, TTL 60s) ──▶ Redis Cache Miss 시 PostgreSQL 집계
   └── 쓰기/변경/삭제 작업 ──▶ findByIdAndUserId 소유권 검증 ──▶ PostgreSQL 반영 + @CacheEvict 즉시 무효화
```

---

## API 요약

| Method | Endpoint | 설명 | 인증 필요 | 비고 |
| --- | --- | --- | :---: | --- |
| `POST` | `/api/users/signup` | 회원가입 | X | 이메일 중복 검증 및 BCrypt 해싱 |
| `POST` | `/api/auth/login` | 로그인 | X | Access / Refresh Token 발급 |
| `POST` | `/api/auth/refresh` | Access Token 재발급 | X | 유효 Refresh Token 검증 |
| `POST` | `/api/auth/logout` | 로그아웃 | X | 저장된 Refresh Token 즉시 폐기 |
| `GET` | `/api/auth/me` | 내 정보 조회 | O | SecurityContext 인증 주체 확인 |
| `POST` | `/api/todos` | Todo 생성 | O | `Idempotency-Key` 지원 + 통계 캐시 무효화 |
| `GET` | `/api/todos` | Todo 목록 조회 | O | 키워드 검색 및 완료 여부 필터 |
| `GET` | `/api/todos/{id}` | Todo 단건 조회 | O | `findByIdAndUserId` 소유권 격리 |
| `PATCH` | `/api/todos/{id}` | Todo 제목 수정 | O | 소유권 격리 + 통계 캐시 무효화 |
| `PATCH` | `/api/todos/{id}/complete` | Todo 완료 처리 | O | 소유권 격리 + 통계 캐시 무효화 |
| `PATCH` | `/api/todos/{id}/uncomplete` | Todo 완료 취소 | O | 소유권 격리 + 통계 캐시 무효화 |
| `DELETE` | `/api/todos/{id}` | Todo 삭제 | O | 소유권 격리 + 통계 캐시 무효화 |
| `GET` | `/api/todos/stats` | 사용자별 Todo 통계 조회 | O | Redis `@Cacheable` (TTL 60초) |
| `GET` | `/actuator/health` | 서버 헬스체크 | X | 무인증 허용 범위 격리 검증 완료 |

상세 요청/응답 명세는 [docs/api.md](docs/api.md)에서 확인할 수 있습니다.

### 표준 에러 응답 형식 (`GlobalExceptionHandler`)

```json
{
  "code": "DUPLICATE_REQUEST",
  "message": "이미 처리 중이거나 처리된 요청입니다."
}
```

---

## 로컬 실행 및 테스트 검증

### 1. 로컬 인프라(PostgreSQL, Redis) 기동 및 서버 실행

```bash
docker compose up -d
./gradlew bootRun
```

- Local Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 2. 전체 단위 및 통합 테스트(60건) 실행

```bash
./gradlew clean test
```

### 3. 컨테이너 이미지 빌드 및 실행

```bash
./gradlew bootJar
docker build -t study-tracker-api .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/study_tracker \
  -e REDIS_URL=redis://host.docker.internal:6379 \
  study-tracker-api
```

---

## 운영 및 아키텍처 문서

- [API 상세 명세서](docs/api.md)
- [운영 Runbook](docs/operations/runbook.md)
- [장애 대응 템플릿](docs/operations/incident-template.md)
- [Todo 통계 Redis 캐싱 및 성능 기록](docs/performance/todo-stats-cache.md)
- [설계 의사결정 및 기술 Q&A](docs/interview/backend-project-qa.md)

