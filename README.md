# Study Tracker API

Java/Spring Boot 기반 학습 Todo 관리 API입니다.

백엔드 취업 준비 과정에서 Java, Spring Boot, JPA, Security, PostgreSQL, Redis, CI/CD, 배포, 운영까지 실무 흐름을 학습하고 구현한 프로젝트입니다.

## 배포 주소

- API Server: https://study-tracker-api-7stm.onrender.com
- Swagger: https://study-tracker-api-7stm.onrender.com/swagger-ui/index.html
- Health Check: https://study-tracker-api-7stm.onrender.com/actuator/health

Render Free 인스턴스는 비활성 상태에서 첫 요청이 느릴 수 있습니다.

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Web | Spring MVC |
| ORM | Spring Data JPA, Hibernate |
| DB | PostgreSQL, H2 Test DB |
| Security | Spring Security, JWT, BCrypt |
| Cache | Redis |
| API Docs | Swagger / OpenAPI |
| Test | JUnit, MockMvc, Mockito |
| CI/CD | GitHub Actions, Render Auto Deploy |
| Deploy | Docker, Render |

## 핵심 기능

- 회원가입 / 로그인
- Access Token / Refresh Token 발급
- JWT 인증 기반 Todo API 보호
- 사용자별 Todo 접근 제어
- Todo 생성, 조회, 수정, 완료, 삭제
- Todo 검색 및 완료 여부 필터
- Todo 통계 API
- Redis 기반 Todo 통계 캐싱
- Idempotency-Key 기반 중복 요청 방지
- Global Exception Handler 기반 에러 응답 표준화
- Swagger JWT 인증 설정
- Actuator health check
- GitHub Actions CI
- Render 배포

## API 요약

| Method | Endpoint | 설명 | 인증 |
| --- | --- | --- | --- |
| POST | `/api/users/signup` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인 | X |
| POST | `/api/auth/refresh` | Access Token 재발급 | X |
| POST | `/api/auth/logout` | 로그아웃 | X |
| GET | `/api/auth/me` | 내 정보 조회 | O |
| POST | `/api/todos` | Todo 생성 | O |
| GET | `/api/todos` | Todo 목록 조회 | O |
| GET | `/api/todos/{id}` | Todo 단건 조회 | O |
| PATCH | `/api/todos/{id}` | Todo 제목 수정 | O |
| PATCH | `/api/todos/{id}/complete` | Todo 완료 | O |
| PATCH | `/api/todos/{id}/uncomplete` | Todo 완료 취소 | O |
| DELETE | `/api/todos/{id}` | Todo 삭제 | O |
| GET | `/api/todos/stats` | Todo 통계 조회 | O |
| GET | `/actuator/health` | 서버 상태 확인 | X |

상세 API는 [docs/api.md](docs/api.md)에서 확인할 수 있습니다.

## 아키텍처

```text
Client
  -> Controller
  -> Service
  -> Repository
  -> PostgreSQL

Todo Stats
  -> Service
  -> Redis Cache
  -> PostgreSQL

Authentication
  -> Spring Security Filter
  -> JWT Token Provider
  -> SecurityContext
```

## 인증 흐름

```text
1. 회원가입
2. 로그인
3. Access Token / Refresh Token 발급
4. Authorization: Bearer ACCESS_TOKEN 으로 Todo API 요청
5. Access Token 만료 시 Refresh Token으로 재발급
6. 로그아웃 시 Refresh Token 삭제
```

## Redis 적용

### Todo 통계 캐싱

`GET /api/todos/stats`는 사용자별 Todo 통계를 조회합니다.

- 첫 요청: DB count 쿼리 실행 후 Redis 캐싱
- 반복 요청: Redis 캐시 응답
- Todo 생성/수정/완료/삭제: 해당 사용자의 통계 캐시 삭제
- TTL: 60초

### 중복 요청 방지

`POST /api/todos`는 `Idempotency-Key` 헤더를 지원합니다.

```http
POST /api/todos
Authorization: Bearer ACCESS_TOKEN
Idempotency-Key: todo-create-001
Content-Type: application/json
```

같은 사용자가 같은 key로 다시 요청하면 `409 Conflict`를 반환합니다.

## 에러 응답 형식

```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지"
}
```

예시:

```json
{
  "code": "DUPLICATE_REQUEST",
  "message": "이미 처리 중이거나 처리된 요청입니다."
}
```

## 로컬 실행

PostgreSQL과 Redis를 Docker Compose로 실행합니다.

```bash
docker compose up -d
./gradlew bootRun
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## 테스트

```bash
./gradlew clean test
```

## 빌드

```bash
./gradlew bootJar
```

## Docker 실행

```bash
docker build -t study-tracker-api .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/study_tracker \
  -e REDIS_URL=redis://host.docker.internal:6379 \
  study-tracker-api
```

운영 프로필로 직접 실행하려면 `SPRING_PROFILES_ACTIVE`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `REDIS_URL` 환경변수가 필요합니다.

## CI/CD

GitHub Actions에서 `main` 브랜치 push 시 자동으로 테스트와 빌드를 실행합니다.

```text
main push
-> GitHub Actions
-> ./gradlew clean test
-> ./gradlew bootJar
-> Render Deploy
```

## 운영 문서

- [운영 Runbook](docs/operations/runbook.md)
- [장애 대응 템플릿](docs/operations/incident-template.md)
- [Todo 통계 성능 개선 기록](docs/performance/todo-stats-cache.md)
- [백엔드 프로젝트 면접 Q&A](docs/interview/backend-project-qa.md)

## 학습/구현 포인트

- Java 객체지향 기반 도메인 모델링
- Spring Boot 계층형 구조
- DTO와 Entity 분리
- JPA 연관관계와 Lazy Loading
- N+1 문제 확인과 fetch join
- JWT 인증과 Spring Security 필터
- 사용자별 리소스 접근 제어
- Refresh Token 저장과 로그아웃
- Redis 캐싱과 캐시 무효화
- Redis 기반 중복 요청 방지
- PostgreSQL 프로필 분리
- Docker 기반 로컬 개발 환경
- GitHub Actions CI
- Render 배포와 Actuator 운영 확인
