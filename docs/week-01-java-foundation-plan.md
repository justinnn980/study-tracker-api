# 1주차 계획: Java 기본기

## 주간 목표

Spring Boot에 들어가기 전에 순수 Java로 작은 프로그램을 완성한다.

이번 주 통과 기준:

- 객체를 상태와 행위로 나누어 만들 수 있다.
- 인터페이스와 구현체를 나눌 수 있다.
- 컬렉션으로 데이터를 저장하고 조회할 수 있다.
- `Optional`, `Stream`, 예외 처리를 어색하지 않게 쓸 수 있다.
- 본인이 만든 코드를 면접 질문에 맞춰 설명할 수 있다.

## Day 1: Todo 도메인

미션 문서:

```text
docs/01-java-todo-domain-mission.md
```

만들 파일:

```text
src/main/java/com/jobprep/studytracker/todo/domain/Todo.java
```

핵심 개념:

- 클래스
- 필드 캡슐화
- 생성자
- `final`
- 상태 변경 메서드
- 입력 검증

완료 기준:

- `./gradlew compileJava` 성공
- `id`, `title`, `completed`의 차이를 설명
- 생성자 검증 이유 설명

## Day 2: Repository

미션 문서:

```text
docs/02-java-todo-repository-mission.md
```

만들 파일:

```text
src/main/java/com/jobprep/studytracker/todo/repository/TodoRepository.java
src/main/java/com/jobprep/studytracker/todo/repository/MemoryTodoRepository.java
```

핵심 개념:

- 인터페이스
- 구현체
- `List`
- `Optional`
- `Stream`

완료 기준:

- 저장, 전체 조회, ID 조회, 삭제 구현
- `findById()`가 `Optional<Todo>`를 반환하는 이유 설명
- Repository를 인터페이스로 나눈 이유 설명

## Day 3: Service

만들 파일:

```text
src/main/java/com/jobprep/studytracker/todo/service/TodoService.java
```

핵심 개념:

- 비즈니스 로직
- 계층 분리
- Repository 의존
- 메서드 책임 분리

구현 기능:

- 할 일 추가
- 전체 목록 조회
- 완료 처리
- 삭제
- 키워드 검색

완료 기준:

- `Main`이나 Repository에 비즈니스 로직이 섞이지 않음
- Service가 Repository 인터페이스에 의존함

## Day 4: Exception

만들 파일:

```text
src/main/java/com/jobprep/studytracker/todo/exception/TodoNotFoundException.java
```

핵심 개념:

- 사용자 정의 예외
- unchecked exception
- 예외 메시지
- 실패 흐름 설계

완료 기준:

- 없는 ID 완료/삭제 시 `TodoNotFoundException` 발생
- `Exception` 같은 넓은 타입으로 뭉개지 않음
- 왜 사용자 정의 예외를 만들었는지 설명

## Day 5: Console Main

만들 파일:

```text
src/main/java/com/jobprep/studytracker/todo/TodoConsoleApplication.java
```

핵심 개념:

- 콘솔 입력
- 메뉴 분기
- 예외 처리
- 사용자가 보는 출력

구현 메뉴:

- 추가
- 전체 조회
- 완료
- 삭제
- 검색
- 종료

완료 기준:

- 콘솔에서 실제로 사용 가능
- 잘못된 입력이 들어와도 프로그램이 바로 종료되지 않음

## Day 6: 리팩터링

정리할 것:

- 메서드 이름
- 중복 코드
- 책임 분리
- 불필요한 setter
- `public`으로 열린 필드
- `Optional.get()` 직접 호출

완료 기준:

- 코드 읽는 순서가 자연스러움
- 각 클래스의 역할을 한 문장으로 설명 가능

## Day 7: 면접 점검

답변할 질문:

- 객체지향이 무엇인가?
- 캡슐화가 왜 필요한가?
- 인터페이스를 왜 쓰는가?
- Repository와 Service의 차이는 무엇인가?
- `List`와 `Map` 중 어떤 것을 선택할 것인가?
- `Optional`은 왜 쓰는가?
- 예외 처리는 어디에서 해야 하는가?
- 이 코드를 Spring Boot로 옮기면 어떤 클래스가 Bean이 되는가?

완료 기준:

- 각 질문에 30초 이상 답변 가능
- 본인 코드 기준으로 설명 가능
- 모르는 개념을 코드와 연결해서 다시 정리

## 이번 주 금지 사항

- Spring 기능 먼저 붙이기
- DB 연결하기
- Lombok으로 코드 줄이기
- 모든 로직을 `main`에 넣기
- 이해 안 된 코드를 복붙하기

## 이번 주 최종 산출물

```text
Todo.java
TodoRepository.java
MemoryTodoRepository.java
TodoService.java
TodoNotFoundException.java
TodoConsoleApplication.java
```

최종 실행:

```bash
./gradlew compileJava
```

추가 목표:

```bash
./gradlew test
```
