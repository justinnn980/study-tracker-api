# 백엔드 취업 로드맵

## 운영 원칙

공부만 하지 않는다. 각 단계마다 코드, 실행 결과, 설명을 남긴다.

합격 기준은 "알고 있다"가 아니라 "만들었고, 왜 그렇게 만들었는지 말할 수 있다"이다.

## 1단계: Java 기본기

목표:

- 객체를 상태와 행위로 나누어 설계한다.
- 컬렉션, 예외, 제네릭, Stream, Optional을 작은 프로그램 안에서 사용한다.
- Spring 없이도 요구사항을 코드로 풀 수 있다.

산출물:

- Todo 콘솔 도메인
- Repository 인터페이스
- MemoryRepository 구현
- Service 계층
- 콘솔 입출력

통과 질문:

- 왜 필드를 `private`으로 감추는가?
- 왜 Repository를 인터페이스로 만드는가?
- `List`, `Map`, `Set`은 각각 언제 적합한가?
- `Optional`을 반환하면 무엇이 좋아지는가?
- checked exception과 unchecked exception의 차이는 무엇인가?

## 2단계: Spring Boot 기본

목표:

- Java Todo 구조를 Spring Boot API 구조로 옮긴다.
- Controller, Service, Repository의 책임을 분리한다.
- DI, IoC, Bean을 코드 기준으로 설명한다.

산출물:

- Study Tracker REST API
- Controller
- Service
- Repository
- Request/Response DTO
- Validation
- Global Exception Handler

통과 질문:

- Controller가 Entity를 바로 반환하면 왜 위험한가?
- Service는 왜 필요한가?
- Spring Bean은 누가 만들고 관리하는가?
- 생성자 주입을 선호하는 이유는 무엇인가?
- Validation 실패 응답은 어떤 형태가 좋은가?

## 3단계: REST API

목표:

- HTTP Method, 상태 코드, URL 설계를 실무적으로 적용한다.
- 성공 응답과 에러 응답의 형식을 일관되게 만든다.

산출물:

- 할 일 생성/조회/수정/삭제 API
- 학습 기록 생성/조회 API
- 일관된 에러 응답
- Swagger 또는 API 문서

통과 질문:

- `POST`, `GET`, `PATCH`, `DELETE`는 각각 언제 쓰는가?
- 생성 성공은 왜 보통 `201 Created`인가?
- URL에 동사를 넣지 않는 이유는 무엇인가?
- DTO를 나누는 기준은 무엇인가?

## 4단계: DB / SQL

목표:

- MySQL로 데이터를 저장하고 조회한다.
- 테이블 설계, JOIN, 인덱스, 트랜잭션을 설명한다.

산출물:

- MySQL 연결
- 학습 주제, 학습 기록 테이블
- 기본 CRUD 쿼리
- JOIN 조회
- 인덱스 적용 실험

통과 질문:

- 기본키와 외래키의 차이는 무엇인가?
- 인덱스는 언제 성능을 망칠 수 있는가?
- 트랜잭션 ACID는 무엇인가?
- 정규화는 왜 하는가?

## 5단계: JPA

목표:

- Entity와 Repository로 DB 작업을 처리한다.
- 영속성 컨텍스트, Lazy Loading, N+1 문제를 코드로 경험한다.

산출물:

- JPA Entity
- Spring Data JPA Repository
- 연관관계 매핑
- Fetch Join 또는 EntityGraph 적용
- QueryDSL 또는 동적 검색 쿼리

통과 질문:

- Entity와 DTO를 분리하는 이유는 무엇인가?
- 영속성 컨텍스트의 1차 캐시는 무엇인가?
- Lazy Loading은 언제 문제가 되는가?
- N+1 문제는 어떻게 확인하고 해결하는가?

## 6단계: 인증 / 보안

목표:

- 로그인, 권한, 토큰 기반 인증을 구현한다.
- Spring Security 흐름을 그림 없이 코드로 설명한다.

산출물:

- 회원가입
- 로그인
- 비밀번호 암호화
- JWT 발급/검증
- 권한별 API 접근 제어

통과 질문:

- 비밀번호를 평문 저장하면 왜 안 되는가?
- Session과 JWT의 차이는 무엇인가?
- 인증과 인가의 차이는 무엇인가?
- Security Filter는 어떤 역할을 하는가?

## 7단계: 배포 / 운영

목표:

- 만든 API를 서버에 올리고 로그를 확인한다.
- 장애가 났을 때 어디부터 봐야 하는지 안다.

산출물:

- Dockerfile
- EC2 배포
- RDS 연결
- Nginx reverse proxy
- HTTPS 적용
- 로그 확인 절차

통과 질문:

- 로컬에서는 되는데 서버에서 안 될 때 무엇을 확인하는가?
- Docker image와 container의 차이는 무엇인가?
- Nginx는 왜 앞단에 두는가?
- 환경변수는 왜 필요한가?

## 8단계: 테스트 / CI/CD

목표:

- 핵심 로직과 API를 테스트로 보호한다.
- GitHub Actions로 자동 검증을 만든다.

산출물:

- 단위 테스트
- 통합 테스트
- MockMvc 테스트
- GitHub Actions CI
- 자동 배포 파이프라인

통과 질문:

- 단위 테스트와 통합 테스트의 차이는 무엇인가?
- Mockito는 언제 쓰는가?
- 테스트가 느려지는 원인은 무엇인가?
- CI가 있으면 무엇이 좋아지는가?

## 9단계: Redis / Kafka / MSA

목표:

- 캐싱, 비동기 처리, 분산 환경의 기본 문제를 경험한다.

산출물:

- Redis 캐시
- 분산락 실험
- Kafka Producer/Consumer
- 비동기 이벤트 처리

통과 질문:

- 캐시는 언제 무효화해야 하는가?
- Redis를 DB처럼 쓰면 왜 위험한가?
- Kafka Topic과 Consumer Group은 무엇인가?
- 비동기 처리는 어떤 단점이 있는가?

## 10단계: 성능 / 대용량 / 모니터링

목표:

- 성능 병목을 측정하고 개선한다.
- 운영 지표를 보고 문제를 추적한다.

산출물:

- 쿼리 튜닝 기록
- 캐싱 전략 문서
- 부하 테스트 결과
- Prometheus/Grafana 대시보드
- 장애 대응 회고

통과 질문:

- 성능 개선 전 무엇을 먼저 측정해야 하는가?
- 쿼리 실행 계획은 어떻게 읽는가?
- 평균 응답 시간만 보면 왜 부족한가?
- 장애 회고에는 무엇을 남겨야 하는가?
