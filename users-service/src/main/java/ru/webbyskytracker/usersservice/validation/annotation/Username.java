package ru.webbyskytracker.usersservice.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.webbyskytracker.usersservice.validation.validator.UsernameValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface Username {
    String message() default "Username must start with a letter and contain only Latin letters and digits";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
