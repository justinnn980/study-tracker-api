# 01. Todo 도메인 셀프 체크

`Todo.java`를 만든 뒤 아래 기준으로 스스로 먼저 확인한다.

## 컴파일 체크

```bash
./gradlew compileJava
```

성공해야 한다.

## 동작 체크

아래 상황을 머릿속으로만 넘기지 말고, 코드가 실제로 이렇게 동작하는지 확인한다.

1. 정상 생성

- 입력: `id = 1L`, `title = "Java 공부"`
- 기대: 객체가 생성된다.
- 기대: `isCompleted()`는 `false`이다.

2. 완료 처리

- 입력: `complete()` 호출
- 기대: `isCompleted()`는 `true`이다.

3. 제목 검증

- 입력: `title = null`
- 기대: `IllegalArgumentException`이 발생한다.

4. 공백 제목 검증

- 입력: `title = "   "`
- 기대: `IllegalArgumentException`이 발생한다.

5. 대소문자 구분 없는 검색

- 제목: `"Java Study"`
- 입력: `matchesKeyword("java")`
- 기대: `true`

6. 없는 키워드 검색

- 제목: `"Java Study"`
- 입력: `matchesKeyword("spring")`
- 기대: `false`

7. 빈 키워드 검색

- 입력: `matchesKeyword("")`
- 기대: `false`

8. 공백 키워드 검색

- 입력: `matchesKeyword("   ")`
- 기대: `false`

9. `null` 키워드 검색

- 입력: `matchesKeyword(null)`
- 기대: `false`

## 자주 나오는 실수

- `title.isBlank()`를 `title == null`보다 먼저 호출한다.
- `completed`를 `final`로 만든다.
- `complete()`가 새 객체를 반환하게 만든다.
- `matchesKeyword()`에서 `keyword` 검증을 하지 않는다.
- 대소문자 처리를 한쪽에만 적용한다.
- getter 대신 필드를 `public`으로 연다.

## 제출 전 설명 연습

아래 답변을 각 2문장 이상으로 말해본다.

- `id`와 `title`은 왜 `final`인가?
- `completed`는 왜 `final`이 아닌가?
- 생성자에서 검증하는 이유는 무엇인가?
- `isBlank()`와 `isEmpty()`는 어떻게 다른가?
- 검색 로직을 `Todo` 안에 둔 이유는 무엇인가?
