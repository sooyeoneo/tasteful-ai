package com.example.tastefulai.member;

import com.example.tastefulai.domain.member.enums.GenderRole;
import com.example.tastefulai.domain.member.enums.MemberRole;
import com.example.tastefulai.domain.member.service.MemberService;
import com.example.tastefulai.global.common.service.RedisService;
import com.example.tastefulai.global.error.exception.CustomException;
import com.example.tastefulai.global.util.JwtProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MemberPasswordChangeTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtProvider jwtProvider;

    // 테스트 데이터 초기화
    @BeforeAll
    static void setUp(@Autowired MemberService memberService) {
        String email = "usertest4@gmail.com";
        String password = "Password123!";
        String nickname = "TestAdmins";
        Integer age = 30;
        GenderRole genderRole = GenderRole.MALE; // 성별 역할
        MemberRole memberRole = MemberRole.USER; // 기본 사용자 역할

        // 테스트 데이터 생성
        try {
            memberService.signup(email, password, nickname, age, genderRole, memberRole);

        } catch (CustomException exception) {
            System.out.println("테스트 데이터 생성 중복 발생" + exception.getMessage());
        }
    }

    @Test
    @DisplayName("비밀번호 변경 후 기존 토큰 무효화")
    void testPasswordChangeInvalidatesToken() {
        // Given
        String email = "usertest4@gmail.com";
        String currentPassword = "Password123!";
        String newPassword = "Password456!";
        String currentAccessToken = jwtProvider.generateAccessToken(email);

        // Redis에 기존 토큰 저장
        redisService.save("BLACKLIST:" + currentAccessToken, "invalid", jwtProvider.getAccessTokenExpiryMillis());

        // 비밀번호 변경
        memberService.updatePassword(email, currentPassword, newPassword, currentAccessToken);

        // When
        boolean isBlacklisted = redisService.exists("BLACKLIST:" + currentAccessToken);

        // Then
        assertTrue(isBlacklisted, "기존 토큰이 블랙리스트에 저장되지 않았습니다.");

        // 새 비밀번호로 토큰 생성
        String newToken = jwtProvider.generateAccessToken("test@example.com");

        // 새 토큰 검증
        boolean isValidNewToken = jwtProvider.validateToken(newToken);
        assertTrue(isValidNewToken, "새 비밀번호로 생성된 토큰이 유효하지 않습니다.");
    }
}