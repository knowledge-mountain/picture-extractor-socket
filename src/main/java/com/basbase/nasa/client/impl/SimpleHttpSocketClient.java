package com.basbase.nasa.client.impl;

import com.basbase.nasa.client.HttpClient;
import com.basbase.nasa.model.HttpStatus;
import com.basbase.nasa.model.Method;
import com.basbase.nasa.model.ResponseWrapper;
import com.basbase.nasa.model.Uri;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.basbase.nasa.client.HttpClientFactoryUtil.openInputReader;
import static com.basbase.nasa.client.HttpClientFactoryUtil.openPrintWriter;
import static com.basbase.nasa.client.HttpClientFactoryUtil.openSocket;
import static com.basbase.nasa.client.HttpClientFactoryUtil.writeToSocket;

public class SimpleHttpSocketClient implements HttpClient {
    @Override
    public <T> ResponseWrapper<T> head(Uri uri) {
        return perform(Method.HEAD, uri, null);
    }

    @Override
    public <T> ResponseWrapper<T> get(Uri uri, Function<BufferedReader, T> bodyExtractor) {
        return perform(Method.GET, uri, bodyExtractor);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T> ResponseWrapper<T> perform(Method method, Uri uri, Function<BufferedReader, T> bodyExtractor) {
        try (var client = openSocket(uri)) {
            try (var out = openPrintWriter(client)) {
                writeToSocket(out, prepareOutput(method, uri).toString());
                try (var reader = openInputReader(client)) {
                    var headLine = reader.readLine();
                    if (headLine.split(" ")[1].startsWith("3")) {
                        return head(Uri.createFrom(getLocation(reader)));
                    }
                    var status = headLine.split(" ")[1];
                    if (!HttpStatus.OK.code().equalsIgnoreCase(status)) {
                        System.exit(1);
                    }
                    var builder = ResponseWrapper.builder()
                            .headers(extractHeaders(reader));
                    if (bodyExtractor != null) {
                        builder.body(bodyExtractor.apply(reader));
                    }
                    return (ResponseWrapper<T>) builder.build();
                }
            }
        }
    }

    @SneakyThrows
    private String getLocation(BufferedReader in) {
        var line = in.readLine();
        while (!line.startsWith("Location")) {
            line = in.readLine();
        }
        return line.split(": ")[1];
    }

    private StringBuilder prepareOutput(Method method, Uri uri) {
        return new StringBuilder()
                .append(method.name()).append(" ").append(uri.getPath()).append(" ").append("HTTP/1.1").append("\n")
                .append("Host: ").append(uri.getHost()).append("\n")
                .append("Connection: close").append("\n")
                .append("\n");
    }

    @SneakyThrows
    private Map<String, String> extractHeaders(BufferedReader in) {
        var headers = new HashMap<String, String>();
        var line = in.readLine();
        while (line != null && !line.isEmpty()) {
            var doubleChunks = line.split(": ");
            var key = doubleChunks[0];
            var value = doubleChunks[1];
            headers.put(key, value);
            line = in.readLine();
        }
        return headers;
    }
}
