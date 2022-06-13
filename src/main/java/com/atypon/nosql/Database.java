package com.atypon.nosql;

import com.atypon.nosql.collection.MultipleFilesMatchedException;
import com.atypon.nosql.collection.NoSuchDocumentException;
import com.atypon.nosql.collection.NoSuchIndexException;
import com.atypon.nosql.document.InvalidDocumentSchema;
import com.atypon.nosql.gsondocument.FieldsDoNotMatchException;
import com.atypon.nosql.keywordsparser.InvalidKeywordException;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public interface Database {
    void createCollection(String collectionName, String schemaString)
            throws InvalidKeywordException, InvalidDocumentSchema, CollectionAlreadyExists;

    void deleteCollection(String collectionName) throws CollectionNotFoundException;

    void addDocument(String collectionName, String documentString)
            throws IOException, DocumentSchemaViolationException, CollectionNotFoundException;

    void updateDocument(String collectionName, String documentID, String updatedDocumentString)
            throws MultipleFilesMatchedException, IOException, NoSuchDocumentException,
            DocumentSchemaViolationException, CollectionNotFoundException;

    Collection<Map<String, Object>> readDocuments(String collectionName, String matchDocumentString)
            throws FieldsDoNotMatchException, IOException, CollectionNotFoundException;

    void deleteDocuments(String collectionName, String matchDocumentString)
            throws FieldsDoNotMatchException, IOException, CollectionNotFoundException;

    Collection<Map<String, Object>> getCollectionIndexes(String collectionName)
            throws CollectionNotFoundException, IOException;

    void createIndex(String collectionName, String indexDocumentString)
            throws IOException, CollectionNotFoundException;

    void deleteIndex(String collectionName, String indexDocumentString)
            throws CollectionNotFoundException, NoSuchIndexException;

    Collection<String> getCollectionsNames();

    Map<String, Object> getCollectionSchema(String collectionName) throws CollectionNotFoundException;
}
