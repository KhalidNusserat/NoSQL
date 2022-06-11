package com.atypon.nosql.document;

import com.atypon.nosql.gsondocument.GsonDocument;

public interface DocumentGenerator<T extends Document<?>> {
    T createFromString(String src);

    GsonDocument appendId(GsonDocument document);
}
