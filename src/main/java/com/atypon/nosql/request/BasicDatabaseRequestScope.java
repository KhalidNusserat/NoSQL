package com.atypon.nosql.request;

import lombok.ToString;

import java.util.regex.Pattern;

@ToString
public class BasicDatabaseRequestScope implements DatabaseRequestScope {

    private final Pattern databasePattern;

    private final Pattern collectionPattern;

    private BasicDatabaseRequestScope(String databaseRegex, String collectionRegex) {
        databasePattern = Pattern.compile(databaseRegex);
        collectionPattern = Pattern.compile(collectionRegex);
    }

    public static BasicRequestScopeBuilder builder() {
        return new BasicRequestScopeBuilder();
    }

    @Override
    public boolean matches(DatabaseRequest request) {
        String database = request.database() == null ? "" : request.database();
        String collection = request.collection() == null ? "" : request.collection();
        boolean matchesDatabase = databasePattern.matcher(database).matches();
        boolean matchesCollection = collectionPattern.matcher(collection).matches();
        return matchesDatabase && matchesCollection;
    }

    public static class BasicRequestScopeBuilder {

        private String databaseRegex = ".+";

        private String collectionRegex = ".+";

        public BasicRequestScopeBuilder databaseRegex(String databaseRegex) {
            this.databaseRegex = databaseRegex;
            return this;
        }

        public BasicRequestScopeBuilder collectionRegex(String collectionRegex) {
            this.collectionRegex = collectionRegex;
            return this;
        }

        public BasicDatabaseRequestScope build() {
            return new BasicDatabaseRequestScope(databaseRegex, collectionRegex);
        }
    }
}
