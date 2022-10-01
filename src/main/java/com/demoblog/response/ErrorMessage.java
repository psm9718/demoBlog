package com.demoblog.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error Message Format
 * $name(code, errorMessage)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    RequestArgumentNotValid(400,"잘못된 요청입니다."),
    DuplicateIdFound(6001,"Duplicate Id"),
    //...
    UnrecognizedRole(6010,"Unrecognized Role");

    private final int code;
    private final String errorMessage;

}
