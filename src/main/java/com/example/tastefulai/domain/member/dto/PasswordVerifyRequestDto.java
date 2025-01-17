package com.example.tastefulai.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordVerifyRequestDto {

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp =  "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()-+=]).{8,}$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.")
    private final String password;

    @JsonCreator
    public PasswordVerifyRequestDto(@JsonProperty("password") String password) {
        this.password = password;
    }
}