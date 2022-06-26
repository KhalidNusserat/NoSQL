package com.atypon.nosql.response;

import lombok.Builder;

import java.util.Collection;
import java.util.Map;

public record DatabaseResponse(String message, Collection<Map<String, Object>> result) {
    @Builder
    public DatabaseResponse {}
}
