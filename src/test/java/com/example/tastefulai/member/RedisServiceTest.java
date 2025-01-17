package com.example.tastefulai.member;

import com.example.tastefulai.global.common.service.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    @DisplayName("Refresh Token 저장 및 TTL 검증")
    void saveAndRetrieveRefreshToken() {
        // Given
        String key = "refreshToken:testUser";
        String refreshToken = "sampleRefreshToken";
        long ttl = 60000L; // 60초

        // When
        redisService.save(key, refreshToken, ttl);
        String storedToken = redisService.get(key);

        // Then
        assertEquals(refreshToken, storedToken);
        assertTrue(redisService.getExpiration(key) <= ttl);
    }

    @Test
    @DisplayName("Access Token 블랙리스트 등록 및 검증")
    void testBlacklistToken() {
        // Given
        String token = "blacklistedToken";

        // When
        redisService.addToBlacklist(token, 10L);
        boolean isBlacklistedImmediately = redisService.isBlacklisted(token);

        // Then
        assertTrue(isBlacklistedImmediately, "Token should be blacklisted immediately after adding.");

        // TTL 만료 후 상태 확인
        try {
            Thread.sleep(6000); // 6초 대기
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt(); // 스레드 상태 복구
            fail("The test was interrupted during sleep: " + exception.getMessage());
        }

        boolean isStillBlacklisted = redisService.isBlacklisted(token);
        assertFalse(isStillBlacklisted, "Token should no longer be blacklisted after TTL expiration.");
    }
}