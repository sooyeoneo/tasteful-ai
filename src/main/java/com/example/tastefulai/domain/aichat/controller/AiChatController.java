package com.example.tastefulai.domain.aichat.controller;

import com.example.tastefulai.domain.aichat.dto.AiChatRequestDto;
import com.example.tastefulai.domain.aichat.dto.AiChatResponseDto;
import com.example.tastefulai.domain.aichat.service.AiChatService;
import com.example.tastefulai.global.common.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/aiChats")
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping
    public ResponseEntity<CommonResponseDto<AiChatResponseDto>> createMenuRecommendation(
            @RequestBody AiChatRequestDto aiChatRequestDto
    ) {
        // 일반 회원 권한 인증 로직 추가 예정

        AiChatResponseDto aiChatResponseDto = aiChatService.createMenuRecommendation(aiChatRequestDto);

        return new ResponseEntity<>(
                new CommonResponseDto<>("AI 메뉴 추천 완료", aiChatResponseDto),
                HttpStatus.OK
        );
    }
}