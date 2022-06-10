package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;

import java.nio.file.Path;
import java.util.Set;

public interface FieldIndexManager<E, T extends Document<E>> {
    FieldIndex<E, T> read(Path indexPath);

    FieldIndex<E, T> create(Set<DocumentField> documentFields, Path collectionsPath, Path indexesPath);

    void update(FieldIndex<E, T> fieldIndex);
}
