# Backend Job Prep

Java/Spring 백엔드 취업 준비용 프로젝트다.

목표는 단순히 공부하는 것이 아니라, 단계마다 코드와 설명을 남기면서 면접에서 말할 수 있는 백엔드 포트폴리오를 만드는 것이다.

## 지금 할 일

현재 단계:

```text
1단계: Java 기본기
```

현재 미션:

```text
Todo 도메인 객체 구현
```

만들 파일:

```text
src/main/java/com/jobprep/studytracker/todo/domain/Todo.java
```

컴파일 확인:

```bash
./gradlew compileJava
```

## 오늘 읽을 문서

1. 현재 진행판

```text
docs/current-coaching-board.md
```

2. 1단계 미션

```text
docs/01-java-todo-domain-mission.md
```

3. 셀프 체크

```text
docs/01-java-todo-domain-self-check.md
```

## 제출 형식

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

## 전체 로드맵

```text
docs/00-backend-job-roadmap.md
```

## 1주차 계획

```text
docs/week-01-java-foundation-plan.md
```

## 코치 리뷰 기준

```text
docs/coach-review-rubric.md
```

## 현재 금지 사항

- Spring Controller 만들기
- DB 연결하기
- JPA Entity 만들기
- Lombok 사용하기
- 모든 로직을 `main`에 넣기
- 이해하지 못한 코드 복붙하기

지금은 `Todo.java` 하나를 제대로 만드는 것이 목표다.
