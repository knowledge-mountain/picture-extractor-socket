package com.basbase.nasa.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record ResponseWrapper<T>(Map<String, String> headers, T body) {
}
