package com.demoblog.exception;

public class InvalidRequest extends RuntimeException {
    public InvalidRequest(String fieldName, String message) {
        super(fieldName + " : " + message);
    }
}
