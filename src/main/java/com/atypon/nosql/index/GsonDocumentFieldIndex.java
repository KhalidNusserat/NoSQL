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

    private final Path path;

    public GsonDocumentFieldIndex(Set<DocumentField> documentFields, Path path) {
        this.documentFields = documentFields;
        this.path = path;
        pathToValues = new ReversedHashMap<>();
        translatedDocumentFields = new HashSet<>(documentFields);
        if (documentFields.contains(DocumentField.of("_matchID"))) {
            translatedDocumentFields.remove(DocumentField.of("_matchID"));
            translatedDocumentFields.add(DocumentField.of("_id"));
        }
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

    @Override
    public Path getPath() {
        return path;
    }
}
