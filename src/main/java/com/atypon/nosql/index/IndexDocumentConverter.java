package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;

public interface IndexDocumentConverter {
    Index toIndex(Document indexDocument);

    Document toDocument(Index index);
}
