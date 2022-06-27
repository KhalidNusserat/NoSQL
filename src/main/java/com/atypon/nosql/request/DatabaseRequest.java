package com.atypon.nosql.request;

import lombok.Builder;

public record DatabaseRequest(String database, String collection, DatabaseOperation operation, Payload payload) {
    @Builder
    public DatabaseRequest {
    }
}
