package com.atypon.nosql.request.filters;

import com.atypon.nosql.request.DatabaseRequest;

public abstract class DatabaseRequestFilter {
    public abstract DatabaseRequest applyOn(DatabaseRequest request);
}
