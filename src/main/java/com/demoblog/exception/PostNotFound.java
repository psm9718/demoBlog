package com.demoblog.exception;

public class PostNotFound extends DemoBlogException {

    private static final String MESSAGE ="존재하지 않는 글입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }
    public PostNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
