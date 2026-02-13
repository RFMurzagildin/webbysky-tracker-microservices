package ru.webbyskytracker.usersservice.service;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    public void saveCodeInRedis(String prefix, String email, String code, long ttlSeconds){
        String key = prefix + email;
        redisTemplate.opsForValue().set(key, code, ttlSeconds, TimeUnit.SECONDS);
        log.info("Value({}) with key({}) added to Redis", code, email);
    }

    public String getCodeFromRedis(String prefix, String email) throws RedisConnectionException {
        return redisTemplate.opsForValue().get(prefix + email);
    }

    public void deleteCodeFromRedis(String prefix, String email){
        String key = prefix + email;
        redisTemplate.delete(key);
        log.info("Key({}) and its value deleted from Redis", key);
    }

    public boolean isValid(String prefix, String email, String code){
        String storedCode = getCodeFromRedis(prefix, email);
        if(storedCode != null && storedCode.equals(code)){
            log.info("Key({}) with value({}) is available in Redis", email, code);
            return true;
        }
        log.info("Key({}) with value({}) is  not available in Redis or the code is incorrect", email, code);
        return false;
    }

    public String generateNewCode(){
        return String.format("%05d", new Random().nextInt(100000));
    }
}
