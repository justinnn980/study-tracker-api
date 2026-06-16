# 백엔드 개발자 이력서 초안

## 기본 정보

**이름**: [이름 입력]  
**이메일**: [이메일 입력]  
**전화번호**: [전화번호 입력]  
**GitHub**: https://github.com/justinnn980  
**Portfolio Project**: https://github.com/justinnn980/study-tracker-api  
**배포 URL**: https://study-tracker-api-7stm.onrender.com  

## 지원 직무

Java / Spring Boot 백엔드 개발자

## 자기소개

Java와 Spring Boot 기반의 백엔드 개발을 학습하며, 단순 CRUD를 넘어 인증, 권한 제어, JPA, PostgreSQL, Redis, CI/CD, 배포와 운영까지 하나의 흐름으로 구현해본 백엔드 개발자 지원자입니다.

Study Tracker API 프로젝트를 통해 JWT 인증, 사용자별 리소스 접근 제어, Redis 캐싱과 중복 요청 방지, GitHub Actions CI, Render 배포, Actuator 기반 health check를 직접 구현했습니다. 기능 구현에서 끝내지 않고 테스트와 운영 문서까지 남기는 개발 습관을 갖추는 것을 목표로 하고 있습니다.

## 기술 스택

### Backend

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Security
- Bean Validation
- Global Exception Handler

### Database / ORM

- PostgreSQL
- H2 Test DB
- Spring Data JPA
- Hibernate
- Entity Mapping
- Lazy Loading
- Fetch Join
- Index 설계

### Auth / Security

- JWT Access Token
- Refresh Token
- BCrypt Password Encoding
- Security Filter
- Authorization Header 인증
- 사용자별 리소스 접근 제어

### Cache / Performance

- Redis
- Spring Cache
- TTL 기반 캐싱
- Cache Eviction
- Idempotency-Key 기반 중복 요청 방지
- Todo 통계 API 캐싱

### Test / DevOps

- JUnit
- MockMvc
- Mockito
- GitHub Actions
- Docker
- Docker Compose
- Render
- Actuator Health Check

## 프로젝트

### Study Tracker API

**기간**: 2026.06  
**역할**: 개인 프로젝트 / 백엔드 전체 구현  
**GitHub**: https://github.com/justinnn980/study-tracker-api  
**배포**: https://study-tracker-api-7stm.onrender.com  
**Swagger**: https://study-tracker-api-7stm.onrender.com/swagger-ui/index.html  

Java와 Spring Boot로 구현한 학습 Todo 관리 백엔드 API입니다. 회원가입, 로그인, JWT 인증, Todo CRUD, 사용자별 접근 제어, Redis 캐싱, 중복 요청 방지, CI/CD, 배포와 운영 확인까지 백엔드 실무 흐름을 학습하고 구현했습니다.

#### 주요 구현

- Spring Security와 JWT를 사용해 로그인, Access Token, Refresh Token 인증 구현
- BCrypt를 사용해 사용자 비밀번호 암호화
- Refresh Token을 DB에 저장하고, 재발급과 로그아웃 시 토큰 삭제 처리
- `@AuthenticationPrincipal` 기반으로 현재 로그인 사용자를 식별
- Todo 조회, 수정, 삭제 시 `todoId + userId` 조건으로 조회해 다른 사용자의 데이터 접근 차단
- Spring Data JPA와 PostgreSQL을 사용해 User, Todo, RefreshToken Entity와 Repository 구현
- Lazy Loading 관계에서 N+1 문제가 발생하는 상황을 테스트로 확인하고 fetch join으로 개선
- Redis를 사용해 Todo 통계 API 결과를 60초 동안 캐싱
- Todo 생성, 수정, 완료, 삭제 시 사용자별 통계 캐시를 삭제하는 캐시 무효화 처리
- `Idempotency-Key`를 Redis에 저장해 같은 생성 요청의 중복 처리를 방지
- Global Exception Handler로 에러 응답 형식 표준화
- Swagger/OpenAPI에 JWT 인증 설정 적용
- GitHub Actions로 `main` 브랜치 push 시 테스트와 빌드 자동 실행
- Docker와 Render를 사용해 API 서버 배포
- Actuator `/actuator/health`를 통해 서버 상태 확인 가능하도록 구성
- 운영 Runbook과 장애 대응 템플릿 작성

#### 기술적 고민과 해결

**사용자별 데이터 접근 제어**

Todo API는 단순히 Todo id만으로 조회하지 않고, 항상 현재 로그인한 사용자의 id와 함께 조회하도록 구현했습니다. 이 방식으로 다른 사용자의 Todo id를 알고 있어도 조회, 수정, 삭제할 수 없도록 했습니다.

```java
Optional<Todo> findByIdAndUserId(Long id, Long userId);
```

**Redis 캐싱과 캐시 무효화**

Todo 통계 API는 반복 조회 가능성이 높다고 판단해 Redis 캐시를 적용했습니다. 다만 Todo 데이터가 변경되면 통계가 달라질 수 있기 때문에, Todo 생성/수정/완료/삭제 시 해당 사용자의 통계 캐시를 삭제하도록 했습니다.

**중복 요청 방지**

Todo 생성 API에 `Idempotency-Key`를 적용했습니다. 같은 사용자가 같은 key로 다시 요청하면 Redis에 저장된 key를 확인해 `409 Conflict`를 반환하도록 했습니다. 이를 통해 버튼 중복 클릭이나 네트워크 재시도에 의한 중복 생성을 방지했습니다.

**운영 확인**

Actuator health endpoint를 열어 배포 후 서버가 정상 상태인지 확인할 수 있도록 했습니다. 또한 Render 로그, 환경변수, DB/Redis 연결 상태를 확인하는 운영 Runbook을 작성했습니다.

#### 테스트

- 회원가입 / 로그인 Controller 테스트
- Todo CRUD Controller 테스트
- 사용자별 Todo 접근 제어 테스트
- Todo Service 테스트
- JPA Repository 테스트
- JWT 유효/만료/잘못된 토큰 테스트
- Refresh Token 만료 테스트
- Redis 캐시 동작과 캐시 무효화 테스트
- Actuator health 공개 접근 테스트

#### CI/CD

```text
main push
-> GitHub Actions
-> ./gradlew clean test
-> ./gradlew bootJar
-> Render Deploy
```

## 학습 정리

- Java 객체지향과 도메인 모델링
- Spring Boot 계층형 구조
- Controller, Service, Repository 역할 분리
- DTO와 Entity 분리
- JPA Entity, Repository, 연관관계
- Lazy Loading과 N+1 문제
- Spring Security와 JWT 인증
- 사용자별 권한 제어
- PostgreSQL 프로필 분리
- Redis 캐싱과 TTL
- Idempotency-Key 기반 중복 요청 방지
- GitHub Actions CI
- Render 배포
- Actuator 기반 운영 확인

## 강점

- 기능 구현에서 끝내지 않고 테스트, 배포, 운영 문서까지 연결해본 경험
- 인증, 권한, 캐싱, 중복 요청 방지처럼 실제 서비스에서 자주 필요한 백엔드 기능을 직접 구현
- 에러 응답 표준화와 테스트 코드를 통해 유지보수 가능한 구조를 지향
- 새로운 개념을 코드로 구현한 뒤 문서와 면접 답변으로 정리하는 학습 방식

## 교육

**[학교명 또는 교육 과정명 입력]**  
[전공 또는 과정명 입력]  
[기간 입력]

## 자격증

- [자격증이 있으면 입력]

## 기타 경험

- [이전 직무/아르바이트/팀 프로젝트/개인 프로젝트가 있으면 입력]

## 이력서 제출 전 수정해야 할 항목

- 이름
- 이메일
- 전화번호
- 학력
- 교육 과정
- 자격증
- 이전 경험
- 지원 회사 공고의 주요 키워드

## 공고별 맞춤 수정 가이드

### Java / Spring Boot 공고

- Spring Boot, JPA, Spring Security, REST API, 테스트 경험을 앞쪽에 배치합니다.

### Redis / 성능 우대 공고

- Todo 통계 API 캐싱, TTL, Cache Eviction, Idempotency-Key 중복 요청 방지를 강조합니다.

### 운영 경험 우대 공고

- Render 배포, GitHub Actions CI, Actuator health check, 운영 Runbook을 강조합니다.

### 테스트 코드 우대 공고

- MockMvc, Service 테스트, Repository 테스트, JWT 테스트, 캐시 테스트를 강조합니다.
