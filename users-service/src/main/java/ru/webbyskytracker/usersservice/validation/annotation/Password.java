package ru.webbyskytracker.usersservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.webbyskytracker.usersservice.validation.validator.PasswordValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {
    String message() default "Password must be 8-25 characters and contain uppercase, lowercase, and digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}