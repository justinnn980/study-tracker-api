# DB Schema

다음 단계에서 메모리 저장소를 MySQL과 JPA로 교체할 때 사용할 초안입니다.

## todos

```sql
CREATE TABLE todos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Column Meaning

| Column | Type | Meaning |
| --- | --- | --- |
| id | BIGINT | Todo 식별자 |
| title | VARCHAR(255) | 할 일 제목 |
| completed | BOOLEAN | 완료 여부 |
| created_at | DATETIME | 생성 시간 |
| updated_at | DATETIME | 마지막 수정 시간 |

## Next JPA Entity Shape

```java
@Entity
@Table(name = "todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed;
}
```
