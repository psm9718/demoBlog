package com.demoblog.response;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordCheck {

    String message() default "password validation check";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
