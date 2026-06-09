package com.jobprep.studytracker.todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void todo_생성_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java 공부\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java 공부"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void todo_생성_실패_title이_비어있음() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("할 일 제목은 비어 있을 수 없습니다."));
    }

    @Test
    void todo_전체_조회_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Spring 공부\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring 공부"))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    void todo_완료_처리_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"완료 테스트\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1/complete")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("완료 테스트"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void 존재하지_않는_todo_조회_실패() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(get("/api/todos/999")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Todo를 찾을 수 없습니다. id=999"));
    }

    @Test
    void todo_삭제_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"삭제 테스트\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void todo_제목_수정_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java 공부\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Spring 공부\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Spring 공부"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void todo_제목_수정_실패_title이_비어있음() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java 공부\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("할 일 제목은 비어 있을 수 없습니다."));
    }

    @Test
    void todo_완료_취소_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"완료 취소 테스트\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1/complete")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(patch("/api/todos/1/uncomplete")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("완료 취소 테스트"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void todo_keyword_검색_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java 공부\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"운동하기\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/todos?keyword=java")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Java 공부"));
    }

    @Test
    void todo_completed_true_필터_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"완료할 일\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"미완료할 일\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1/complete")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/todos?completed=true")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("완료할 일"))
                .andExpect(jsonPath("$[0].completed").value(true));
    }

    @Test
    void todo_통계_조회_성공() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"완료할 일\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"미완료할 일\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1/complete")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/todos/stats")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.completedCount").value(1))
                .andExpect(jsonPath("$.activeCount").value(1));
    }

    @Test
    void 존재하지_않는_todo_수정_실패() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(patch("/api/todos/999")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"없는 Todo 수정\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Todo를 찾을 수 없습니다. id=999"));
    }

    @Test
    void todo_제목_수정_후_다시_조회해도_반영됨() throws Exception {
        String authorization = authorizationHeader();

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"수정 전\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"수정 후\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정 후"));
    }

    @Test
    void 다른_사용자의_todo는_목록에_보이지_않음() throws Exception {
        String ownerAuthorization = authorizationHeader("owner@example.com", "소유자");
        String otherAuthorization = authorizationHeader("other@example.com", "다른 사용자");

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, ownerAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"소유자의 Todo\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, otherAuthorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void 다른_사용자의_todo_조회_실패() throws Exception {
        String ownerAuthorization = authorizationHeader("owner@example.com", "소유자");
        String otherAuthorization = authorizationHeader("other@example.com", "다른 사용자");

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, ownerAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"소유자의 Todo\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, otherAuthorization))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Todo를 찾을 수 없습니다. id=1"));
    }

    @Test
    void 다른_사용자의_todo_수정_실패() throws Exception {
        String ownerAuthorization = authorizationHeader("owner@example.com", "소유자");
        String otherAuthorization = authorizationHeader("other@example.com", "다른 사용자");

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, ownerAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"소유자의 Todo\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, otherAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"수정 시도\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_NOT_FOUND"));
    }

    @Test
    void 다른_사용자의_todo_삭제_실패() throws Exception {
        String ownerAuthorization = authorizationHeader("owner@example.com", "소유자");
        String otherAuthorization = authorizationHeader("other@example.com", "다른 사용자");

        mockMvc.perform(post("/api/todos")
                        .header(HttpHeaders.AUTHORIZATION, ownerAuthorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"소유자의 Todo\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, otherAuthorization))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TODO_NOT_FOUND"));

        mockMvc.perform(get("/api/todos/1")
                        .header(HttpHeaders.AUTHORIZATION, ownerAuthorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("소유자의 Todo"));
    }

    private String authorizationHeader() throws Exception {
        return authorizationHeader("todo-user@example.com", "Todo 사용자");
    }

    private String authorizationHeader(String email, String name) throws Exception {
        signup(email, name);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password123"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = response.split("\"accessToken\":\"")[1].split("\"")[0];
        return "Bearer " + accessToken;
    }

    private void signup(String email, String name) throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password123",
                                  "name": "%s"
                                }
                                """.formatted(email, name)))
                .andExpect(status().isCreated());
    }
}
