package com.atypon.nosql.storage;

import com.atypon.nosql.collection.Stored;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.Encryptor;
import com.atypon.nosql.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SecureStorageEngine implements StorageEngine {

    private final static String secret = ".1d{OXmGv0iu?}>:.5'GQRQQm2W0\\>SE";

    private final StorageEngine storageEngine;

    private SecureStorageEngine(StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
    }

    public static SecureStorageEngine secure(StorageEngine storageEngine) {
        return new SecureStorageEngine(storageEngine);
    }

    @Override
    public Stored<Document> writeDocument(Document document, Path directory) {
        Document encryptedDocument = getEncryptedDocument(document);
        Path storedDocumentPath = storageEngine.writeDocument(encryptedDocument, directory).path();
        return Stored.createStoredObject(document, storedDocumentPath);
    }

    private Document getEncryptedDocument(Document document) {
        String verification = getVerification(document);
        String encryptedDocumentContent = Encryptor.encrypt(document.toString());
        return Document.of("verification", verification, "content", encryptedDocumentContent);
    }

    @NotNull
    private String getVerification(Document document) {
        return Hashing.sha256().hashString(
                document.hashCode() + secret,
                StandardCharsets.UTF_8
        ).toString();
    }

    @Override
    public Optional<Document> readDocument(Path documentPath) {
        Optional<Document> optionalDocument = storageEngine.readDocument(documentPath);
        if (optionalDocument.isPresent()) {
            Document securedDocument = optionalDocument.get();
            return decryptDocument(securedDocument);
        } else {
            return Optional.empty();
        }
    }

    private Optional<Document> decryptDocument(Document securedDocument) {
        Map<String, Object> map = securedDocument.toMap();
        String expectedVerification = (String) map.get("verification");
        String encryptedContent = (String) map.get("content");
        String decryptedContent = Encryptor.decrypt(encryptedContent);
        if (!isValidJSON(decryptedContent)) {
            return Optional.empty();
        } else {
            Document document = Document.fromJson(decryptedContent);
            String recievedVerification = getVerification(document);
            if (expectedVerification.equals(recievedVerification)) {
                return Optional.of(document);
            } else {
                return Optional.empty();
            }
        }
    }

    private boolean isValidJSON(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue(json, Map.class);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    @Override
    public void deleteFile(Path documentPath) {
        storageEngine.deleteFile(documentPath);
    }

    @Override
    public Stored<Document> updateDocument(Document updatedDocument, Path documentPath) {
        Document encryptedDocument = getEncryptedDocument(updatedDocument);
        Path updatedDocumentPath = storageEngine.updateDocument(encryptedDocument, documentPath).path();
        return Stored.createStoredObject(updatedDocument, updatedDocumentPath);
    }

    @Override
    public List<Document> readDocumentsDirectory(Path directoryPath) {
        return FileUtils.traverseDirectory(directoryPath)
                .map(this::readDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
