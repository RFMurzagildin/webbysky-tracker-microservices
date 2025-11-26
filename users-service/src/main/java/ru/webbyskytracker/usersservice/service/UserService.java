package ru.webbyskytracker.usersservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.webbyskytracker.usersservice.dto.response.ErrorResponse;
import ru.webbyskytracker.usersservice.dto.request.RegistrationUserDto;
import ru.webbyskytracker.usersservice.dto.response.UserDtoResponse;
import ru.webbyskytracker.usersservice.entity.User;
import ru.webbyskytracker.usersservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponseEntity<?> save(RegistrationUserDto dto){
        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            return new ResponseEntity<>(new ErrorResponse("Password and confirmation do not match"), HttpStatus.UNAUTHORIZED);
        }
        //делаем стальные проверки(валидация username, mail и password, проверка, что такого username и mail не существует в базе)

        //хэшируем пароль
        User savedUser = userRepository.save(new User(
                dto.getUsername(),
                dto.getMail(),
                dto.getPassword()
        ));

        UserDtoResponse result = new UserDtoResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getMail(),
                "The user was successfully created"
        );

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
