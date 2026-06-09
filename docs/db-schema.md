# DB Schema

현재 프로젝트는 PostgreSQL과 Spring Data JPA를 사용합니다.

테스트 환경에서는 H2를 PostgreSQL 모드로 실행합니다.

## users

| Column | Type | 설명 |
| --- | --- | --- |
| id | BIGINT | PK |
| email | VARCHAR | 로그인 이메일, unique |
| password | VARCHAR | BCrypt 암호화 비밀번호 |
| name | VARCHAR | 사용자 이름 |

## todos

| Column | Type | 설명 |
| --- | --- | --- |
| id | BIGINT | PK |
| title | VARCHAR | Todo 제목 |
| completed | BOOLEAN | 완료 여부 |
| user_id | BIGINT | users FK |
| created_at | TIMESTAMP | 생성 시간 |
| updated_at | TIMESTAMP | 수정 시간 |

## refresh_tokens

| Column | Type | 설명 |
| --- | --- | --- |
| id | BIGINT | PK |
| token | VARCHAR | Refresh Token, unique |
| user_id | BIGINT | users FK |
| expires_at | TIMESTAMP | 만료 시간 |

## Relationships

```text
users 1 --- N todos
users 1 --- N refresh_tokens
```

## Indexes

| Table | Index | Columns | 목적 |
| --- | --- | --- | --- |
| todos | idx_todos_user_id_id | user_id, id | 사용자별 Todo 목록 조회 |
| todos | idx_todos_user_id_completed_id | user_id, completed, id | 사용자별 완료 여부 필터 조회 |
| users | unique email | email | 이메일 중복 가입 방지 |
| refresh_tokens | unique token | token | Refresh Token 단건 조회 |

## JPA Mapping

- `Todo -> User`: `@ManyToOne(fetch = FetchType.LAZY)`
- `User -> Todo`: `@OneToMany(mappedBy = "user")`
- `RefreshToken -> User`: `@ManyToOne(fetch = FetchType.LAZY)`

## 권한 제어 쿼리

Todo 단건 조회, 수정, 삭제는 단순히 Todo id만 보지 않고 `todoId + userId` 조건으로 조회합니다.

```java
Optional<Todo> findByIdAndUserId(Long id, Long userId);
```

이 방식으로 다른 사용자의 Todo 접근을 차단합니다.
