package com.basbase.nasa.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Arrays.stream;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class Uri {

    private final Scheme scheme;
    private final String host;
    private final int port;
    private final String path;

    public static Uri createFrom(String url) {
        var chunks = Pattern.compile("(.*?)://(.*?)(/.*)")
                .matcher(url)
                .results()
                .map(mr -> format("%s %s %s", mr.group(1), mr.group(2), mr.group(3)))
                .flatMap(s -> stream(s.split(" ")))
                .toList();

        var scheme = stream(Scheme.values())
                .filter(s -> chunks.get(0).equalsIgnoreCase(s.name()))
                .findFirst()
                .orElseThrow();

        return Uri.builder()
                .scheme(scheme)
                .host(chunks.get(1))
                .port(scheme.getPort())
                .path(chunks.get(2))
                .build();
    }
}