package com.basbase.nasa.model;

import lombok.Builder;

@Builder
public record Pair(Long length, String url) {
}