# 백엔드 프로젝트 면접 Q&A

## Q. 이 프로젝트를 한 문장으로 설명해보세요.

학습 Todo 관리 API를 Java와 Spring Boot로 구현했고, JWT 인증, 사용자별 권한 제어, JPA, PostgreSQL, Redis 캐싱, CI/CD, Render 배포, Actuator 운영 확인까지 적용한 백엔드 포트폴리오 프로젝트입니다.

## Q. Spring Security를 어떻게 사용했나요?

JWT 기반 인증을 적용했습니다. 로그인 성공 시 Access Token과 Refresh Token을 발급하고, 이후 요청은 `Authorization: Bearer` 헤더의 Access Token을 검증합니다. 검증된 사용자 정보는 SecurityContext에 저장하고, Controller에서는 `@AuthenticationPrincipal`로 현재 사용자를 가져옵니다.

## Q. Access Token과 Refresh Token은 왜 나눴나요?

Access Token은 API 요청 인증에 사용하고, 짧은 만료 시간을 둡니다. Refresh Token은 Access Token을 재발급하기 위한 토큰으로 DB에 저장합니다. 로그아웃 시 Refresh Token을 삭제해서 이후 재발급을 막습니다.

## Q. 다른 사용자의 Todo 접근을 어떻게 막았나요?

Todo 조회, 수정, 삭제 시 단순히 Todo id만 보지 않고 `todoId + userId` 조건으로 조회했습니다. 해당 사용자의 Todo가 아니면 조회 결과가 없으므로 404 응답을 반환합니다.

## Q. JPA Lazy Loading은 어디에 적용했나요?

`Todo -> User`, `RefreshToken -> User` 관계에 Lazy Loading을 적용했습니다. 필요한 시점에 연관 엔티티를 조회하게 해서 불필요한 즉시 로딩을 피했습니다.

## Q. N+1 문제는 어떻게 확인했나요?

User와 Todo의 Lazy Loading 관계에서 전체 사용자와 Todo 목록을 조회할 때 추가 쿼리가 발생하는 상황을 테스트로 확인했습니다. 이후 fetch join을 사용하는 조회 메서드를 추가해 쿼리 수 차이를 비교했습니다.

## Q. Redis는 어디에 사용했나요?

Todo 통계 API에 캐싱을 적용했습니다. 반복 조회 시 DB count 쿼리를 줄이고, Todo 생성/수정/삭제/완료 시 해당 사용자의 캐시를 삭제하도록 했습니다. 또한 `Idempotency-Key`를 Redis에 저장해 중복 생성 요청을 방지했습니다.

## Q. 캐시 데이터가 오래되면 어떻게 하나요?

통계 캐시는 TTL을 60초로 설정했고, Todo가 생성/수정/완료/삭제될 때 해당 사용자의 캐시를 삭제합니다. 그래서 반복 조회 성능을 얻으면서도 데이터 변경 후 오래된 통계가 계속 내려가지 않도록 했습니다.

## Q. Idempotency-Key는 왜 필요한가요?

사용자가 버튼을 여러 번 누르거나 네트워크 재시도로 같은 생성 요청이 중복 전송될 수 있습니다. `Idempotency-Key`를 Redis에 5분 동안 저장해서 같은 사용자의 같은 생성 요청이 다시 들어오면 `409 Conflict`로 막았습니다.

## Q. GitHub Actions는 어떤 역할을 하나요?

`main` 브랜치에 push하면 GitHub Actions가 `./gradlew clean test`와 `./gradlew bootJar`를 실행합니다. 테스트와 빌드가 통과한 코드만 배포 흐름으로 넘어가게 해서 실수를 줄였습니다.

## Q. 운영 관점에서 무엇을 했나요?

Actuator의 health endpoint를 열어 서버 상태를 확인할 수 있게 했고, Render 로그와 환경변수, DB/Redis 연결 상태를 확인하는 운영 Runbook을 작성했습니다.

## Q. 이 프로젝트에서 가장 실무적인 부분은 무엇인가요?

인증, 권한 제어, 캐싱, 중복 요청 방지, CI, 배포, 운영 확인까지 연결한 점입니다. 단순 CRUD에서 끝내지 않고 실제 서비스에서 자주 문제가 되는 인증, 사용자 데이터 격리, 반복 조회 성능, 배포 후 상태 확인까지 다뤘습니다.
