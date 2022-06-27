package com.atypon.nosql.request;

public interface DatabaseRequestScope {
    boolean matches(DatabaseRequest request);
}
