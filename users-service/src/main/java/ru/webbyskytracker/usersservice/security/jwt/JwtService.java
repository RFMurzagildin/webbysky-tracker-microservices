package ru.webbyskytracker.usersservice.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.webbyskytracker.usersservice.dto.response.JwtAuthDto;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value(value = "${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token.expiration-minutes:30}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-days:7}")
    private long refreshTokenExpirationDays;

    /**
     * Генерирует пару токенов при логине.
     */
    public JwtAuthDto generateAuthToken(String email, Long userId, String role){
        String accessToken = generateToken(email, userId, role, accessTokenExpiration, ChronoUnit.MINUTES);
        String refreshToken = generateToken(email, userId, role, refreshTokenExpirationDays, ChronoUnit.DAYS);
        // сохранить refreshToken в Redis
        return new JwtAuthDto(accessToken, refreshToken);
    }

    /**
     * Генерирует НОВУЮ пару токенов при обновлении.
     * Важно: выдаётся НОВЫЙ refresh token (rotation).
     */
    public JwtAuthDto refreshAuthToken(String email, Long userId, String role){
        String accessToken = generateToken(email, userId, role, accessTokenExpiration, ChronoUnit.MINUTES);
        String refreshToken = generateToken(email, userId, role, refreshTokenExpirationDays, ChronoUnit.DAYS);
        return new JwtAuthDto(accessToken, refreshToken);
    }

    /**
     * Универсальный метод для генерации токена
     */
    private String generateToken(String email, Long userId, String role, long amount, ChronoUnit unit){
        Instant now = Instant.now();
        Instant expiry = now.plus(amount, unit);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuer("user-service")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет, валиден ли токен (не истёк, подпись верна).
     * Возвращает true/false — НЕ бросает исключения.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.error("Invalid JWT: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("JWT signature validation failed", e);
        }
        return false;
    }

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Получение email из токена
     */
    public String getEmailFromToken(String token){
        return parseClaims(token).getSubject();
    }

    /**
     * Получение userId из токена
     */
    public Long getUserIdFromToken(String token){
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * Получение role из токена
     */
    public String getRoleFromToken(String token){
        return parseClaims(token).get("role", String.class);
    }



}
