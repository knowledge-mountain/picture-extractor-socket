package com.basbase.nasa.client;

import com.basbase.nasa.model.ResponseWrapper;
import com.basbase.nasa.model.Uri;

import java.io.BufferedReader;
import java.util.function.Function;

public interface HttpClient {
    <T> ResponseWrapper<T> head(Uri uri);
    <T> ResponseWrapper<T> get(Uri uri, Function<BufferedReader, T> bodyExtractor);
}
