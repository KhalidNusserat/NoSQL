package com.atypon.nosql;

public interface Database {
    void createCollection(String collectionName);

    void removeCollection(String collectionName);

    void addDocument(String collectionName, String documentString);

    String readDocuments(String collectionName, String matchDocumentString);

    String updateDocument(String documentID, String updatedDocumentString);

    void removeDocuments(String collection, String matchDocumentString);
}
