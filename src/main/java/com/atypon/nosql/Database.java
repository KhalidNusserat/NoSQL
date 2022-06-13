package com.atypon.nosql;

import com.atypon.nosql.collection.MultipleFilesMatchedException;
import com.atypon.nosql.collection.NoSuchDocumentException;
import com.atypon.nosql.document.InvalidDocumentSchema;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;

import java.io.IOException;
import java.util.Collection;

public interface Database {
    void createCollection(String collectionName, String schemaString)
            throws InvalidKeywordException, InvalidDocumentSchema;

    void removeCollection(String collectionName);

    void addDocument(String collectionName, String documentString) throws IOException, DocumentSchemaViolationException;

    void updateDocument(String collectionName, String documentID, String updatedDocumentString)
            throws MultipleFilesMatchedException, IOException, NoSuchDocumentException, DocumentSchemaViolationException;

    Collection<String> readDocuments(String collectionName, String matchDocumentString)
            throws FieldsDoNotMatchException, IOException;

    void deleteDocuments(String collectionName, String matchDocumentString) throws FieldsDoNotMatchException, IOException;

    void createIndex(String collectionName, String indexDocumentString) throws IOException;
}
