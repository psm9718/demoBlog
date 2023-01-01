package com.demoblog.exception;

public class Unauthorized extends DemoBlogException {

    private static final String MESSAGE = "인증되지 않는 접근입니다.";
    public Unauthorized() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
