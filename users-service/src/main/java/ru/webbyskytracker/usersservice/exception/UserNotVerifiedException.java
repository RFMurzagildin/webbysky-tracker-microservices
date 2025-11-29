package ru.webbyskytracker.usersservice.exception;

public class UserNotVerifiedException extends RuntimeException{
    public UserNotVerifiedException(String message) {
        super(message);
    }
}
