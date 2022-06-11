package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.index.FieldIndex;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public interface IndexedDocumentsCollection<T extends Document<?>> extends DocumentsCollection<T> {
    void createIndex(Set<DocumentField> documentFields);

    void deleteIndex(Set<DocumentField> documentFields);

    Collection<Set<DocumentField>> getIndexes();
}
