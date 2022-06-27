package com.atypon.nosql.request;

import com.atypon.nosql.document.Document;

import java.util.regex.Pattern;

public class BasicDatabaseRequestScope implements DatabaseRequestScope {

    private final Pattern databasePattern;

    private final Pattern collectionPattern;

    private final Document target;

    private BasicDatabaseRequestScope(String databaseRegex, String collectionRegex, Document target) {
        this.target = target;
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
        Document criteria = request.payload() == null || request.payload().criteria() == null ?
                Document.of() : request.payload().criteria();
        boolean matchesDatabase = databasePattern.matcher(database).matches();
        boolean matchesCollection = collectionPattern.matcher(collection).matches();
        boolean targetsDocument = target.subsetOf(criteria);
        return matchesDatabase && matchesCollection && targetsDocument;
    }

    public static class BasicRequestScopeBuilder {

        private String databaseRegex = ".+";

        private String collectionRegex = ".+";

        private Document target = Document.of();

        public BasicRequestScopeBuilder databaseRegex(String databaseRegex) {
            this.databaseRegex = databaseRegex;
            return this;
        }

        public BasicRequestScopeBuilder collectionRegex(String collectionRegex) {
            this.collectionRegex = collectionRegex;
            return this;
        }

        public BasicRequestScopeBuilder target(Document target) {
            this.target = target;
            return this;
        }

        public BasicDatabaseRequestScope build() {
            return new BasicDatabaseRequestScope(databaseRegex, collectionRegex, target);
        }
    }
}
