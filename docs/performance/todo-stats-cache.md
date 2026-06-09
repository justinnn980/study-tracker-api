# Todo 통계 API 성능 개선 기록

## 대상 API

GET /api/todos/stats

## 개선 목표

Todo 통계 조회 시 매번 DB count 쿼리를 실행하지 않고, Redis 캐시를 사용해 반복 조회 성능을 개선한다.

## 적용 전 구조

- 요청마다 DB에서 전체 Todo 수 조회
- 요청마다 DB에서 완료 Todo 수 조회
- 반복 조회 시에도 같은 count 쿼리 실행

## 적용 후 구조

- 첫 요청은 DB 조회 후 Redis에 60초 캐싱
- 같은 사용자의 반복 요청은 Redis에서 응답
- Todo 생성, 수정, 완료, 삭제 시 해당 사용자의 통계 캐시 삭제

## 캐시 키

```text
todoStats::USER_ID
```

## 인덱스

```sql
idx_todos_user_id_id(user_id, id)
idx_todos_user_id_completed_id(user_id, completed, id)
```

Todo 목록 조회는 항상 사용자 단위로 제한된다. 그래서 `user_id`를 첫 번째 컬럼으로 두고, 완료 여부 필터 조회를 위해 `completed`를 함께 둔다.

## 측정 방법

```bash
curl -w "\ntime_total=%{time_total}\n" \
  -H "Authorization: Bearer ACCESS_TOKEN" \
  https://study-tracker-api-7stm.onrender.com/api/todos/stats
```

## 검증 결과

| 구분 | 기대 동작 | 검증 방법 | 결과 |
| --- | --- | --- | --- |
| 첫 번째 통계 조회 | DB count 쿼리 후 캐시 저장 | `TodoStatsCacheTest.todo_통계는_캐시된다` | 통과 |
| 두 번째 통계 조회 | 캐시된 값 반환 | DB에 직접 Todo 추가 후 통계 재조회 | 통과 |
| Todo 생성 후 통계 조회 | 캐시 삭제 후 재계산 | `TodoStatsCacheTest.todo가_변경되면_통계_캐시가_삭제된다` | 통과 |

## 배포 후 실측 기록

| 구분 | 응답 시간 | 설명 |
| --- | ---: | --- |
| 첫 번째 요청 | 기록 | 캐시 없음 |
| 두 번째 요청 | 기록 | Redis 캐시 사용 |
| Todo 생성 후 요청 | 기록 | 캐시 삭제 후 재계산 |

## 검증 테스트

- `TodoStatsCacheTest.todo_통계는_캐시된다`
- `TodoStatsCacheTest.todo가_변경되면_통계_캐시가_삭제된다`

## 배운 점

- 캐시는 원본 데이터가 아니라 빠른 조회를 위한 임시 데이터다.
- 캐시 TTL을 두면 오래된 데이터가 무한히 남지 않는다.
- 데이터 변경 시 캐시 삭제 전략이 없으면 오래된 응답을 내려줄 수 있다.
- 인덱스는 자주 조회하는 조건에 맞춰 설계해야 한다.
