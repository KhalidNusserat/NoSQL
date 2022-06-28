package com.atypon.nosql.request;

public enum DatabaseOperation {
    ADD_DOCUMENT(true),
    READ_DOCUMENTS(false),
    REMOVE_DOCUMENTS(true),
    UPDATE_DOCUMENTS(true),
    CREATE_DATABASE(true),
    REMOVE_DATABASE(true),
    GET_DATABASES(false),
    CREATE_COLLECTION(true),
    REMOVE_COLLECTION(true),
    GET_COLLECTIONS(false),
    CREATE_INDEX(true),
    REMOVE_INDEX(true),
    GET_INDEXES(false);

    public final boolean mutatesState;

    DatabaseOperation(boolean mutatesState) {
        this.mutatesState = mutatesState;
    }
}
