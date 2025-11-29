package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.usersservice.dto.response.UserInfo;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.exception.UserNotFoundException;
import ru.webbyskytracker.usersservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
}
