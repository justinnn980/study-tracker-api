# Todo 통계 API 및 N+1 조회 성능 개선 기록

## 1. 대상 구간

1. `GET /api/todos/stats` (`TodoService.findStats`) — 사용자별 전체/완료/미완료 Todo 통계 조회
2. `UserQueryService.findUserTodoSummaries` — 사용자 목록 및 연관 Todo 요약 조회 (`Lazy Loading` N+1 구간)

## 2. 개선 목표

- Todo 통계 반복 조회 시 매번 실행되던 2건의 DB `COUNT` 쿼리를 사용자별 캐시(`@Cacheable`)로 제거하고, 모든 쓰기/상태 변경 경로에서 `@CacheEvict`로 즉시 무효화해 정합성을 보장한다.
- 사용자 단위 조회 패턴에 맞춰 복합 인덱스를 설계하고, 연관 엔티티 목록 조회의 `1 + N` 쿼리를 `LEFT JOIN FETCH` 단일 쿼리로 축소한다.

## 3. 복합 인덱스 및 캐시 설계

### 복합 인덱스 (`Todo.java`)

```sql
idx_todos_user_id_id(user_id, id)
idx_todos_user_id_completed_id(user_id, completed, id)
```

모든 Todo 목록 및 통계 조회는 `user_id` 조건으로 격리되므로 선행 컬럼을 `user_id`로 배치하고, 완료 여부 필터 및 `countByUserIdAndCompleted` 집계를 커버하도록 `(user_id, completed, id)` 인덱스를 구성했다.

### 캐시 키 및 무효화 정책 (`TodoService.java`)

- 캐시 키: `todoStats::{userId}` (운영 Redis TTL 60초)
- 캐시 적재: `@Cacheable(value = "todoStats", key = "#userId")`
- 즉시 무효화(`@CacheEvict`): `add`, `updateTitle`, `complete`, `uncomplete`, `delete` 5개 상태 변경 메서드 전체 적용

## 4. 정량 실측 결과 (Hibernate `Statistics` 통합 테스트 기준)

### A. 통계 캐시 적용 전/후 비교 (`TodoStatsCacheTest`)

- **측정 조건**: 단일 사용자 Todo **5,000건**(완료 2,500건 / 미완료 2,500건) 적재 후 `findStats(userId)` **100회 연속 호출**

| 구분 | 실행 SQL 수 (`PreparedStatement`) | 평균 응답 시간 | 비고 |
| --- | ---: | ---: | --- |
| 캐시 미적용 시 (100회 호출 환산) | **200회** (호출당 `COUNT` 2회) | `9.33 ms` / 회 | 매 요청마다 전체/완료 `COUNT` 실행 |
| 1차 요청 (Cache Miss) | **2회** | `9.33 ms` | DB 집계 후 캐시 저장 |
| 2~100차 요청 (Cache Hit 99회) | **0회** (누적 총 **2회**, **99.0% 감소**) | **`0.02 ms`** | DB 접근 없이 캐시에서 즉시 반환 |

### B. JPA `LEFT JOIN FETCH` N+1 개선 비교 (`UserQueryServiceTest`)

- **측정 조건**: 사용자 **50명** (사용자당 Todo 5건, 총 **250건**) 영속성 컨텍스트 초기화 후 요약 목록 조회

| 구분 | 실행 SQL 수 (`PreparedStatement`) | 응답 시간 | 감소율 |
| --- | ---: | ---: | --- |
| 일반 Lazy Loading 조회 (`findAll`) | **51회** (`1 + 50`) | `16.45 ms` | 기준 |
| `LEFT JOIN FETCH` 조회 (`findAllWithTodos`) | **1회** | **`6.80 ms`** | **SQL 98.0% 감소 / 시간 58.7% 단축** |

## 5. 자동화 검증 테스트

- `TodoStatsCacheTest.todo_통계는_캐시된다`
- `TodoStatsCacheTest.todo가_변경되면_통계_캐시가_삭제된다`
- `TodoStatsCacheTest.todo_완료_처리_시_통계_캐시가_즉시_무효화된다`
- `TodoStatsCacheTest.todo_완료_취소_시_통계_캐시가_즉시_무효화된다`
- `TodoStatsCacheTest.todo_제목_수정_시_통계_캐시가_즉시_무효화된다`
- `TodoStatsCacheTest.todo_삭제_시_통계_캐시가_즉시_무효화된다`
- `TodoStatsCacheTest.반복_통계_조회_100회_시_캐시_적중으로_DB_쿼리가_200회에서_2회로_99퍼센트_감소한다`
- `UserQueryServiceTest.사용자_50명_조회_시_fetch_join으로_SQL_쿼리가_51회에서_1회로_98퍼센트_감소한다`
