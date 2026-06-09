package com.jobprep.studytracker.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
        String email,

        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
        String password,

        @NotBlank(message = "이름은 비어 있을 수 없습니다.")
        String name
) {
}
