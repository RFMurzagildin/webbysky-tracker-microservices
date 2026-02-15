package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.usersservice.dto.response.ApiResponse;
import ru.webbyskytracker.usersservice.dto.response.UserInfo;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.exception.UserNotFoundException;
import ru.webbyskytracker.usersservice.repository.UserRepository;
import ru.webbyskytracker.usersservice.security.jwt.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserInfo getUserInfo(){
        String currentUserEmail = getCurrentUserEmail();
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public String getCurrentUserEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            throw new IllegalStateException("No authenticated user");
        }
        return auth.getName();
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<?> validateToken(String authorizationHeader){
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                if (jwtService.validateToken(token)) {
                    return new ResponseEntity<>(new ApiResponse(true, "True token"), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new ApiResponse(true, "Invalid or expired token"), HttpStatus.UNAUTHORIZED);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(true, "Invalid token"), HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(new ApiResponse(true, "Missing token"), HttpStatus.UNAUTHORIZED);
    }


}
