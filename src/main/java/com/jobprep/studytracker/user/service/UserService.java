package com.jobprep.studytracker.user.service;

import com.jobprep.studytracker.user.domain.User;
import com.jobprep.studytracker.user.exception.DuplicateEmailException;
import com.jobprep.studytracker.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }

        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.save(new User(email, encodedPassword, name));
    }
}
