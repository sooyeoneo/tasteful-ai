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
        return redisTemplate.hasKey("blacklist:" + token); // 키 형식을 통일
    }


    //**** Test Code 사용 메서드 ****//

    // Redis에 데이터 저장
    public void save(String key, String value, long ttlMillis) {
        redisTemplate.opsForValue().set(key, value, ttlMillis, TimeUnit.MILLISECONDS);
    }

    // Redis에서 데이터 조회
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    // Redis에서 데이터 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Redis에 해당 키가 존재하는지 확인
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Redis에서 키의 TTL 확인
    public Long getExpiration(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS); // TTL 값을 밀리초 단위로 반환
    }
}