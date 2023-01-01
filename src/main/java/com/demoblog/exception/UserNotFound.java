package com.demoblog.exception;

public class UserNotFound extends DemoBlogException{

    private static final String MESSAGE = "회원을 찾을 수 없습니다.";
    public UserNotFound() {
        super(MESSAGE);
    }


    @Override
    public int getStatusCode() {
        return 404;
    }
}
