package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    public void save(String refreshToken, Long userId, Duration exp){
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, String.valueOf(userId), exp);
        log.info("Value({}) with key({}) added to Redis", userId, key);
    }

    public boolean exists(String refreshToken){
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void delete(String refreshToken){
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
        log.info("Key({}) and its value deleted from Redis", key);
    }

    public String getUserId(String refreshToken){
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }
}
