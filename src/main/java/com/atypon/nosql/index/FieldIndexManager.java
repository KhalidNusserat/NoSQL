package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;

import java.nio.file.Path;
import java.util.Set;

public interface FieldIndexManager<E, T extends Document<E>> {
    FieldIndex<E, T> loadFieldIndex(Path indexPath, Path documentsPath);

    FieldIndex<E, T> createNewFieldIndex(Set<DocumentField> documentFields, Path collectionsPath, Path indexesPath);

    Path writeFieldIndex(Set<DocumentField> documentFields, Path indexesPath);

    void removeFieldIndex(Path indexPath);
}
