# 현재 코칭 보드

## 현재 단계

1단계: Java 기본기

현재 미션:

- `Todo` 도메인 객체 구현

아직 하지 않는 것:

- Spring Controller
- Service
- Repository
- DB 연결
- JPA Entity
- Security 설정

지금은 백엔드 취업 준비의 첫 바닥을 까는 단계다.

## 오늘 만들 파일

```text
src/main/java/com/jobprep/studytracker/todo/domain/Todo.java
```

## 오늘 완료 조건

- `Todo.java`를 직접 구현한다.
- `./gradlew compileJava`가 성공한다.
- 아래 5개 질문에 본인 말로 답할 수 있다.

질문:

- 왜 `id`와 `title`은 `final`인가?
- 왜 `completed`는 `final`이 아닌가?
- 왜 생성자에서 `title`을 검증하는가?
- `isBlank()`와 `isEmpty()`는 어떻게 다른가?
- 왜 검색 로직이 `Todo` 안에 있는가?

## 제출 순서

1. `01-java-todo-domain-mission.md`를 읽는다.
2. `Todo.java`를 만든다.
3. `01-java-todo-domain-self-check.md`로 확인한다.
4. `./gradlew compileJava`를 실행한다.
5. 코드와 실행 결과를 제출한다.

## 제출할 때 붙일 내용

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

## 코치 판단 기준

통과:

- 컴파일 성공
- 필드 캡슐화
- 생성자 검증
- 상태 변경 메서드가 명확함
- 검색 로직이 자연스러움
- 본인 말로 설명 가능

재제출:

- 컴파일 실패
- 필드가 `public`
- `title == null`보다 `title.isBlank()`를 먼저 호출
- `completed`가 `final`
- `matchesKeyword()`가 `null` 키워드에서 터짐
- 설명 없이 코드만 제출

## 다음 미션

`Todo.java` 통과 후:

- `TodoRepository` 인터페이스
- `MemoryTodoRepository` 구현
- `List`, `Optional`, `Stream` 사용

다음 미션 문서:

```text
docs/02-java-todo-repository-mission.md
```
