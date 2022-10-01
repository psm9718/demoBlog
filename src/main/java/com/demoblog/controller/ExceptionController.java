package com.demoblog.controller;

import com.demoblog.response.ErrorResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintDefinitionException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String errorMessage = e.getMessage();
        log.error("MethodArgumentNotValidException : {}", errorMessage);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("400")
                .errorMessage("잘못된 요청입니다.")
                .build();

        List<FieldError> fieldErrors = e.getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errorResponse;
    }

}
