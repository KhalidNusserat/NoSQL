package com.atypon.nosql.document;

import com.atypon.nosql.gsondocument.GsonDocumentSchema;

public interface DocumentSchemaFactory {
    DocumentSchema createFromDocument(Document schemaDocument);

    @SuppressWarnings("unchecked")
    GsonDocumentSchema createFromClass(Class<?> clazz);
}
