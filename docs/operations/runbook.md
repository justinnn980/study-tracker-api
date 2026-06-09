# 운영 Runbook

## 서버 상태 확인

```bash
curl https://study-tracker-api-7stm.onrender.com/actuator/health
```

정상 응답:

```json
{"status":"UP"}
```

## 장애 확인 순서

1. Render Web Service Logs 확인
2. 최근 배포 커밋 확인
3. `/actuator/health` 응답 확인
4. Postgres 연결 상태 확인
5. Redis Key Value 연결 상태 확인
6. 환경변수 누락 여부 확인

## 자주 나는 문제

| 증상 | 예상 원인 | 확인 위치 |
| --- | --- | --- |
| 서버 시작 실패 | 환경변수 누락 | Render Logs |
| DB 연결 실패 | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` 오류 | Render Environment |
| Redis 연결 실패 | `REDIS_URL` 오류 | Render Environment |
| 401 응답 | JWT 누락 또는 만료 | API 요청 헤더 |
| 409 응답 | 중복 `Idempotency-Key` | 요청 헤더 |

## Render에서 볼 것

- Logs
- Events
- Metrics
- Environment Variables
- 최근 배포 커밋

## 배포 후 확인 명령어

```bash
curl -i https://study-tracker-api-7stm.onrender.com/actuator/health
curl -i https://study-tracker-api-7stm.onrender.com/swagger-ui/index.html
```
