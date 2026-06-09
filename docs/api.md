# API Documentation

Study Tracker API의 주요 REST API 목록입니다.

배포 Swagger:

```text
https://study-tracker-api-7stm.onrender.com/swagger-ui/index.html
```

로컬 Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## 공통 인증

인증이 필요한 API는 Access Token을 `Authorization` 헤더에 넣습니다.

```http
Authorization: Bearer ACCESS_TOKEN
```

## User

### Signup

```http
POST /api/users/signup
Content-Type: application/json
```

요청:

```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "사용자"
}
```

성공 응답: `201 Created`

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "사용자"
}
```

## Auth

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

요청:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

성공 응답: `200 OK`

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "사용자",
  "accessToken": "ACCESS_TOKEN",
  "refreshToken": "REFRESH_TOKEN"
}
```

### Refresh

```http
POST /api/auth/refresh
Content-Type: application/json
```

요청:

```json
{
  "refreshToken": "REFRESH_TOKEN"
}
```

성공 응답: `200 OK`

```json
{
  "accessToken": "NEW_ACCESS_TOKEN"
}
```

### Logout

```http
POST /api/auth/logout
Content-Type: application/json
```

요청:

```json
{
  "refreshToken": "REFRESH_TOKEN"
}
```

성공 응답: `204 No Content`

### Me

```http
GET /api/auth/me
Authorization: Bearer ACCESS_TOKEN
```

성공 응답: `200 OK`

```json
{
  "id": 1,
  "email": "user@example.com"
}
```

## Todo

### Create Todo

```http
POST /api/todos
Authorization: Bearer ACCESS_TOKEN
Idempotency-Key: todo-create-001
Content-Type: application/json
```

요청:

```json
{
  "title": "Java 공부"
}
```

성공 응답: `201 Created`

```json
{
  "id": 1,
  "title": "Java 공부",
  "completed": false
}
```

같은 사용자가 같은 `Idempotency-Key`로 다시 요청하면 `409 Conflict`를 반환합니다.

```json
{
  "code": "DUPLICATE_REQUEST",
  "message": "이미 처리 중이거나 처리된 요청입니다."
}
```

### Find Todos

```http
GET /api/todos
GET /api/todos?keyword=Java
GET /api/todos?completed=true
GET /api/todos?keyword=Java&completed=false
Authorization: Bearer ACCESS_TOKEN
```

성공 응답: `200 OK`

```json
[
  {
    "id": 1,
    "title": "Java 공부",
    "completed": false
  }
]
```

### Find Todo

```http
GET /api/todos/{id}
Authorization: Bearer ACCESS_TOKEN
```

성공 응답: `200 OK`

```json
{
  "id": 1,
  "title": "Java 공부",
  "completed": false
}
```

다른 사용자의 Todo는 조회할 수 없으며 `404 Not Found`를 반환합니다.

### Update Todo Title

```http
PATCH /api/todos/{id}
Authorization: Bearer ACCESS_TOKEN
Content-Type: application/json
```

요청:

```json
{
  "title": "Spring Boot 공부"
}
```

성공 응답: `200 OK`

### Complete Todo

```http
PATCH /api/todos/{id}/complete
Authorization: Bearer ACCESS_TOKEN
```

성공 응답: `200 OK`

### Uncomplete Todo

```http
PATCH /api/todos/{id}/uncomplete
Authorization: Bearer ACCESS_TOKEN
```

성공 응답: `200 OK`

### Delete Todo

```http
DELETE /api/todos/{id}
Authorization: Bearer ACCESS_TOKEN
```

성공 응답: `204 No Content`

### Todo Stats

```http
GET /api/todos/stats
Authorization: Bearer ACCESS_TOKEN
```

성공 응답: `200 OK`

```json
{
  "totalCount": 2,
  "completedCount": 1,
  "activeCount": 1
}
```

## Operations

### Health Check

```http
GET /actuator/health
```

성공 응답: `200 OK`

```json
{
  "status": "UP"
}
```

## Error Response

검증 실패 응답: `400 Bad Request`

```json
{
  "code": "VALIDATION_ERROR",
  "message": "할 일 제목은 비어 있을 수 없습니다."
}
```

존재하지 않는 Todo 응답: `404 Not Found`

```json
{
  "code": "TODO_NOT_FOUND",
  "message": "Todo를 찾을 수 없습니다. id=999"
}
```

중복 요청 응답: `409 Conflict`

```json
{
  "code": "DUPLICATE_REQUEST",
  "message": "이미 처리 중이거나 처리된 요청입니다."
}
```
