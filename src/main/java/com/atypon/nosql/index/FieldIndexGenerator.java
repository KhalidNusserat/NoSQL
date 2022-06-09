package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentField;

import java.nio.file.Path;
import java.util.Set;

public interface FieldIndexGenerator<T extends Document<?>> {
    FieldIndex<T> read(Path indexPath);

    FieldIndex<T> create(Set<DocumentField> documentFields, Path directoryPath);
}
