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
    private static final String KEY_PREFIX = "verification:";
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    public void saveCodeInRedis(String mail, String code, long ttlSeconds){
        try{
            redisTemplate.opsForValue().set(KEY_PREFIX + mail, code, ttlSeconds, TimeUnit.SECONDS);
            log.info("Value({}) with key({}) added to Redis", code, mail);
        }catch(RedisConnectionException e){
            throw new RedisConnectionException("Unable to connect to Redis with root cause");
        }
    }

    public String getCodeFromRedis(String mail) throws RedisConnectionException {
        return redisTemplate.opsForValue().get(KEY_PREFIX + mail);
    }

    public void deleteCodeFromRedis(String mail){
        try{
            redisTemplate.delete(KEY_PREFIX + mail);
            log.info("Key({}) and its value deleted from Redis", mail);
        }catch (RedisConnectionException e){
            throw new RedisConnectionException("Unable to connect to Redis with root cause");
        }

    }

    public boolean isValid(String mail, String code){
        String storedCode = getCodeFromRedis(mail);
        if(storedCode != null && storedCode.equals(code)){
            log.info("Key({}) with value({}) is available in Redis", mail, code);
            return true;
        }
        log.info("Key({}) with value({}) is  not available in Redis or the code is incorrect", mail, code);
        return false;
    }

    public String generateNewCode(){
        return String.format("%05d", new Random().nextInt(100000));
    }
}
