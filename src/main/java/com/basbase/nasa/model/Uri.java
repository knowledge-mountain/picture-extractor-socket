package com.basbase.nasa.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.of;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class Uri {
    private final Scheme scheme;
    private final String host;
    private final int port;
    private final String path;

    public static Uri createFrom(String url) {
        if (url == null || url.isEmpty()) {
            throw new NullPointerException("Url is blank");
        }
        var chunks = Pattern.compile("(.*?)://(.*)")
                .matcher(url)
                .results()
                .flatMap(mr -> of(mr.group(1), mr.group(2)))
                .toList();
        if (!isValid(chunks)) {
            throw new RuntimeException("Invalid URL: [doesn't much to template]");
        }

        var scheme = stream(Scheme.values())
                .filter(s -> chunks.get(0).equalsIgnoreCase(s.name()))
                .findFirst()
                .orElseThrow();
        var host = chunks.get(1).split("/")[0];
        var path = stream(chunks.get(1).split("/"))
                .skip(1)
                .collect(Collectors.joining("/", "/", ""));
        return Uri.builder()
                .scheme(scheme)
                .host(host)
                .port(scheme.getPort())
                .path(path)
                .build();
    }

    private static boolean isValid(List<String> list) {
        return switch (list.size()) {
            case 0, 1 -> false;
            default -> list.stream().allMatch(chunk -> chunk != null && !chunk.isEmpty());
        };
    }
}