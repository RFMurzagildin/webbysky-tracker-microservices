package ru.webbyskytracker.usersservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;
import ru.webbyskytracker.usersservice.security.model.UserDetailsImpl;
import ru.webbyskytracker.usersservice.security.service.UserDetailsServiceImpl;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null) {
            if (jwtService.validateToken(token)) {
                setAuthentication(token);
            } else {
                log.warn("Invalid JWT token");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token){
        try {
            String email = jwtService.getEmailFromToken(token);
            UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth =
                    UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Authentication set for user: {}", email);
        }catch (Exception e){
            log.error("Failed to set authentication for token", e);
            SecurityContextHolder.clearContext();
        }
    }

    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

}
