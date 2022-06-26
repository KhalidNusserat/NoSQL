package com.atypon.nosql.request;

import org.springframework.stereotype.Component;

@Component
public class DummyDatabaseRequestFormatter implements DatabaseRequestFormatter {
    @Override
    public DatabaseRequest format(DatabaseRequest request) {
        return request;
    }
}
