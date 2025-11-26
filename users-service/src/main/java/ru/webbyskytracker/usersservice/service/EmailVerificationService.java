package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "verification:";
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    public void saveCode(String mail, String code, long ttlSeconds){
        redisTemplate.opsForValue().set(KEY_PREFIX + mail, code, ttlSeconds, TimeUnit.SECONDS);
        log.info("Value({}) with key({}) added to Redis", code, mail);
    }

    public String getCode(String mail){
        return redisTemplate.opsForValue().get(KEY_PREFIX + mail);
    }

    public void deleteCode(String mail){
        redisTemplate.delete(KEY_PREFIX + mail);
        log.info("Key({}) and its value deleted from Redis", mail);
    }

    public boolean isValid(String mail, String code){
        String storedCode = getCode(mail);
        if(storedCode != null && storedCode.equals(code)){
            log.info("Key({}) with value({}) is available in Redis", mail, code);
            return true;
        }
        log.info("Key({}) with value({}) is  not available in Redis or the code is incorrect", mail, code);
        return false;
    }
}
