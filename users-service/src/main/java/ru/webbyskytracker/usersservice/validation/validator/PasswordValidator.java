package ru.webbyskytracker.usersservice.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.webbyskytracker.usersservice.validation.annotation.Password;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) return false;
        if (password.length() < 8 || password.length() > 25) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        return password.matches(".*\\d.*");
    }
}
