package com.atypon.nosql.index;

import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.utils.ReversedHashMap;
import com.google.gson.JsonElement;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GsonDocumentFieldIndex implements FieldIndex<JsonElement, GsonDocument> {
    private final Set<DocumentField> documentFields;

    private final Set<DocumentField> translatedDocumentFields;

    private final ReversedHashMap<String, Set<JsonElement>> pathToValues;

    private final String indexPath;

    public GsonDocumentFieldIndex(Set<DocumentField> documentFields, Path indexPath) {
        this.documentFields = documentFields;
        this.indexPath = indexPath.toString();
        pathToValues = new ReversedHashMap<>();
        translatedDocumentFields = new HashSet<>(documentFields);
        if (documentFields.contains(DocumentField.of("_matchID"))) {
            translatedDocumentFields.remove(DocumentField.of("_matchID"));
            translatedDocumentFields.add(DocumentField.of("_id"));
        }
    }

    GsonDocumentFieldIndex(
            Set<DocumentField> documentFields,
            Set<DocumentField> translatedDocumentFields,
            ReversedHashMap<String, Set<JsonElement>> pathToValues,
            String indexPath
    ) {
        this.documentFields = new HashSet<>(documentFields);
        this.translatedDocumentFields = new HashSet<>(translatedDocumentFields);
        this.pathToValues = pathToValues;
        this.indexPath = indexPath;
    }

    public void add(GsonDocument document, Path documentPath) {
        Set<JsonElement> values = document.getAll(translatedDocumentFields);
        pathToValues.put(documentPath.toString(), values);
    }

    public void remove(GsonDocument document) {
        pathToValues.removeByValue(document.getAll(translatedDocumentFields));
    }

    public Collection<String> get(GsonDocument matchDocument) {
        return pathToValues.getFromValue(matchDocument.getAll(documentFields));
    }

    public Set<DocumentField> getDocumentFields() {
        return documentFields;
    }

    public boolean contains(GsonDocument matchDocument) {
        return pathToValues.containsValue(matchDocument.getAll(documentFields));
    }

    public Path getPath() {
        return Path.of(indexPath);
    }
}
