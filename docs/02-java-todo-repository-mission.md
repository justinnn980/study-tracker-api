# 02. Java 기본기 미션: Todo Repository

이 미션은 `01. Todo 도메인`을 통과한 뒤 진행한다.

## 목표

저장소 역할을 인터페이스와 구현체로 나눈다.

Spring Data JPA로 가기 전, Repository가 왜 필요한지 순수 Java로 먼저 익힌다.

## 만들 파일

```text
src/main/java/com/jobprep/studytracker/todo/repository/TodoRepository.java
src/main/java/com/jobprep/studytracker/todo/repository/MemoryTodoRepository.java
```

## TodoRepository 요구사항

패키지:

```java
package com.jobprep.studytracker.todo.repository;
```

필수 메서드:

```java
Todo save(String title)
List<Todo> findAll()
Optional<Todo> findById(Long id)
void deleteById(Long id)
```

## MemoryTodoRepository 요구사항

내부 저장소:

```java
private final List<Todo> todos = new ArrayList<>();
```

ID 생성:

```java
private long sequence = 0L;
```

규칙:

- `save(String title)`은 새 `Todo`를 만든 뒤 저장하고 반환한다.
- 새 Todo의 ID는 `++sequence`로 만든다.
- `findAll()`은 전체 Todo 목록을 반환한다.
- `findById(Long id)`는 `Optional<Todo>`를 반환한다.
- `deleteById(Long id)`는 ID가 일치하는 Todo를 삭제한다.

## 반드시 써야 하는 것

- `interface`
- `implements`
- `List`
- `ArrayList`
- `Optional`
- `Stream`

## 금지

- Spring annotation 사용 금지
- DB 사용 금지
- `Optional.get()` 바로 호출 금지
- `null` 반환 금지

## 통과 질문

- 왜 Repository를 인터페이스로 만들었는가?
- `MemoryTodoRepository`는 무엇에 의존하는가?
- `findById()`는 왜 `Todo`가 아니라 `Optional<Todo>`를 반환하는가?
- `findAll()`에서 내부 리스트를 그대로 반환하면 어떤 문제가 생길 수 있는가?
- `List` 대신 `Map<Long, Todo>`를 쓰면 무엇이 좋아지고 무엇이 나빠지는가?

## 컴파일 확인

```bash
./gradlew compileJava
```

## 제출 양식

```text
[2단계 제출]

1. TodoRepository.java 코드
2. MemoryTodoRepository.java 코드
3. compileJava 결과
4. 위 통과 질문 5개에 대한 내 답변
5. 헷갈린 부분 1개
```
