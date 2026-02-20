package ru.webbyskytracker.usersservice.service;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveCodeInRedis(String prefix, String email, String code, long ttlSeconds){
        String key = prefix + email;
        redisTemplate.opsForValue().set(key, code, ttlSeconds, TimeUnit.SECONDS);
    }

    public String getCodeFromRedis(String prefix, String email) throws RedisConnectionException {
        return redisTemplate.opsForValue().get(prefix + email);
    }

    public void deleteCodeFromRedis(String prefix, String email){
        String key = prefix + email;
        redisTemplate.delete(key);
    }

    public boolean isValid(String prefix, String email, String code){
        String storedCode = getCodeFromRedis(prefix, email);
        return storedCode != null && storedCode.equals(code);
    }

    public String generateNewCode(){
        return String.format("%05d", new Random().nextInt(100000));
    }
}
