package com.demoblog.response;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 패스워드 Validation
 * 대,소문자 포함
 * 특수문자 포함
 * 8자 이상
 */
@Slf4j
public class PasswordValidator implements ConstraintValidator<PasswordCheck, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //대,소문자 포함 8자 이상 체크
        log.warn("value : {}, context : {}", value, context);

        if (value == null) {
            return false;
        }

        return value.length() >=8 &&
                value.chars().boxed()
                .filter(data -> Character.isUpperCase(data)).findAny().isPresent();
    }
}
