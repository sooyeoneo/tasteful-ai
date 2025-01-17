package com.example.tastefulai.global.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Access Token 블랙리스트 등록
    public void addToBlacklist(String token, long ttlMillis) {
        redisTemplate.opsForValue().set("blacklist:" + token, "invalid", ttlMillis, TimeUnit.MILLISECONDS);
    }

    // 블랙리스트에 등록 여부 확인
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }

    // 블랙리스트 확인
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey("BLACKLIST:" + token);
    }
}