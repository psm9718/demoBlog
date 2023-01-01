package com.demoblog.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class DemoBlogException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();


    public DemoBlogException(String message) {
        super(message);
    }

    public DemoBlogException(String message, Throwable cause) {
        super(message, cause);
    }
    public abstract int getStatusCode();

    public void addValidation(String filedName, String message) {
        validation.put(filedName, message);
    }
}
