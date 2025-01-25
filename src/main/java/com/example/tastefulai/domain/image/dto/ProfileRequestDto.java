package com.example.tastefulai.domain.image.dto;

import lombok.Getter;

@Getter
public class ProfileRequestDto {

    private String nickname;

    public ProfileRequestDto(String nickname) {
        this.nickname = nickname;
    }

    public ProfileRequestDto() {
    }
}
