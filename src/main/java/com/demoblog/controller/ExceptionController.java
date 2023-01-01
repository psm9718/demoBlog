package com.demoblog.controller;

import com.demoblog.exception.DemoBlogException;
import com.demoblog.exception.InvalidRequest;
import com.demoblog.exception.PostNotFound;
import com.demoblog.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;
import java.util.Map;

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

    @ResponseBody
    @ExceptionHandler(DemoBlogException.class)
    public ResponseEntity<ErrorResponse> demoBlogException(DemoBlogException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .errorMessage(e.getMessage())
                .build();

        errorResponse.addValidation(e.getValidation());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
    }

    //회원 정보에서 이름에 대해 중복 exception handling (만약 여러 column의 중복을 처리해야 한다면...?)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse dataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException : {}", e.getMessage());


        return ErrorResponse.builder()
                .code("400")
                .errorMessage("중복된 회원 이름입니다.")
                .build();

    }
}
