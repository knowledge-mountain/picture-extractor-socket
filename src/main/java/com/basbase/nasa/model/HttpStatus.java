package com.basbase.nasa.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum HttpStatus {
    OK(200, "OK"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    BAD_REQUEST(400, "Bad Request");

    private final int code;
    @Getter
    private final String message;

    public String code() {
        return String.valueOf(code);
    }
}
