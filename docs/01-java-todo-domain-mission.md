# 01. Java 기본기 미션: Todo 도메인

## 목표

Spring Boot로 들어가기 전에 순수 Java로 도메인 객체를 설계한다.

이번 미션의 합격 기준은 "간단한 객체 하나를 만들고, 왜 그렇게 만들었는지 설명할 수 있다"이다.

## 만들 파일

```text
src/main/java/com/jobprep/studytracker/todo/domain/Todo.java
```

패키지:

```java
package com.jobprep.studytracker.todo.domain;
```

## 구현 요구사항

`Todo`는 아래 상태를 가진다.

```java
private final Long id;
private final String title;
private boolean completed;
```

필수 메서드:

```java
public Todo(Long id, String title)
public void complete()
public boolean matchesKeyword(String keyword)
public Long getId()
public String getTitle()
public boolean isCompleted()
```

## 규칙

- `title`이 `null`이면 `IllegalArgumentException`을 던진다.
- `title`이 공백이면 `IllegalArgumentException`을 던진다.
- 새로 만든 Todo의 `completed` 기본값은 `false`이다.
- `complete()`를 호출하면 `completed`는 `true`가 된다.
- `matchesKeyword()`는 제목에 키워드가 포함되면 `true`를 반환한다.
- 검색은 대소문자를 구분하지 않는다.
- `keyword`가 `null`이거나 공백이면 `false`를 반환한다.

## 금지

- Spring annotation 사용 금지
- Lombok 사용 금지
- 모든 필드를 `public`으로 여는 것 금지
- `Main`부터 만드는 것 금지
- DB, Repository, Service를 먼저 만드는 것 금지

## 컴파일 확인

```bash
./gradlew compileJava
```

## 제출 양식

```text
[1단계 제출]

1. Todo.java 코드

2. compileJava 결과

3. 내 설명
- 왜 id/title은 final인가?
- 왜 completed는 final이 아닌가?
- 왜 title 검증을 생성자에서 하는가?
- isBlank()와 isEmpty()의 차이는 무엇인가?
- matchesKeyword()가 Todo 안에 있는 이유는 무엇인가?

4. 헷갈린 부분 1개
```

## 통과 기준

- 컴파일이 성공한다.
- 필드가 캡슐화되어 있다.
- 생성자에서 유효하지 않은 상태를 막는다.
- 상태 변경 메서드가 명확하다.
- 검색 로직이 도메인 객체 안에 자연스럽게 들어가 있다.
- 위 질문 5개에 본인 말로 답할 수 있다.

## 다음 미션 예고

이 미션을 통과하면 다음은 `TodoRepository` 인터페이스와 `MemoryTodoRepository` 구현이다.

다음 단계에서 배울 것:

- 인터페이스를 왜 쓰는지
- `List`로 저장소를 구현하는 법
- `Optional<Todo>`를 반환하는 이유
- `Stream`으로 검색하는 법
