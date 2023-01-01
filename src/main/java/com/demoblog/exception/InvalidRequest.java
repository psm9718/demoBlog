package com.demoblog.exception;

import lombok.Getter;

@Getter
public class InvalidRequest extends DemoBlogException {

    private static final String MESSAGE = "잘못된 요청입니다.";
    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String key, String value) {
        super(MESSAGE);
        addValidation(key, value);
    }


    @Override
    public int getStatusCode() {
        return 400;
    }
}
