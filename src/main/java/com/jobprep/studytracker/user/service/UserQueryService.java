package com.jobprep.studytracker.user.service;

import com.jobprep.studytracker.user.dto.UserTodoSummaryResponse;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserQueryService {
    private final UserRepository userRepository;

    public UserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserTodoSummaryResponse> findUserTodoSummariesWithNPlusOneForStudy() {
        return userRepository.findAll().stream()
                .map(UserTodoSummaryResponse::from)
                .toList();
    }

    public List<UserTodoSummaryResponse> findUserTodoSummaries() {
        return userRepository.findAllWithTodos().stream()
                .map(UserTodoSummaryResponse::from)
                .toList();
    }
}