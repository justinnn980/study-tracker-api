# Todo API

현재 프로젝트의 Todo REST API 목록입니다.

## Create Todo

```http
POST /api/todos
Content-Type: application/json
```

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

## Find Todos

```http
GET /api/todos
GET /api/todos?keyword=Java
GET /api/todos?completed=true
GET /api/todos?keyword=Java&completed=false
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

## Find Todo

```http
GET /api/todos/{id}
```

성공 응답: `200 OK`

## Update Todo Title

```http
PATCH /api/todos/{id}
Content-Type: application/json
```

```json
{
  "title": "Spring Boot 공부"
}
```

성공 응답: `200 OK`

## Complete Todo

```http
PATCH /api/todos/{id}/complete
```

성공 응답: `200 OK`

## Uncomplete Todo

```http
PATCH /api/todos/{id}/uncomplete
```

성공 응답: `200 OK`

## Delete Todo

```http
DELETE /api/todos/{id}
```

성공 응답: `204 No Content`

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

## Swagger

애플리케이션 실행 후 아래 주소에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```
