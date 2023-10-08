package com.basbase.nasa.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Scheme {
    HTTP(80), HTTPS(443);

    @Getter
    private final int port;

}