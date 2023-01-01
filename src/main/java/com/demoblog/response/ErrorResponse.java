package com.demoblog.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Error 응답 format
 * <p>
 * {
 * "code": "400"
 * "message": "잘못된 요청입니다."
 * "validation": {
 * "title" : "타이틀 값 입력 필수"
 * "content" : "컨텐츠 값 입력 필수"
 * }
 * }
 */

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
//@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

//    private final ErrorMessage errorMessage;
    private final String code;
    private final String errorMessage;
    private final Map<String, String> validation = new HashMap<>();
    private final LocalDateTime timestamp = LocalDateTime.now();


    public void addValidation(String field, String defaultMessage) {
        this.validation.put(field, defaultMessage);
    }
    public void addValidation(Map<String, String> map) {
        validation.putAll(map);
    }

}
