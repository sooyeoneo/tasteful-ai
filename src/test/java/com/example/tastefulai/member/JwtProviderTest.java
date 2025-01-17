package com.example.tastefulai.member;

import com.example.tastefulai.global.util.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("토큰 생성 및 검증")
    void generateAndVeridateToken() {
        // Given
        String email = "usertest1@gmail.com";
        String password = "Password123!";

        // When
        String token = jwtProvider.generateToken(email, jwtProvider.getAccessTokenExpiryMillis());
        boolean isValid = jwtProvider.validateToken(token);

        // Then
        assertTrue(isValid);
        assertEquals(email, jwtProvider.getEmailFromToken(token));
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateExpiredToken() {
        // Given
        String expiredToken = jwtProvider.generateToken("testUser", -1);

        // When
        boolean isValid = jwtProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("잘못된 서명의 토큰 검증 실패")
    void validateTokenWithInvalidSignature() {
        // Given
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0VXNlciJ9.WrongSignature";

        // When
        boolean isValid = jwtProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }
}