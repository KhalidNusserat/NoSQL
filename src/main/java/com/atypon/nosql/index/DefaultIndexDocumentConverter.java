package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefaultIndexDocumentConverter implements IndexDocumentConverter {

    private final Genson genson = new GensonBuilder()
            .useMetadata(true)
            .useRuntimeType(true)
            .create();

    private final DocumentFactory documentFactory;

    public DefaultIndexDocumentConverter(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    @Override
    public Index toIndex(Document indexDocument) {
        return genson.deserialize(indexDocument.toString(), DefaultIndex.class);
    }

    @Override
    public Document toDocument(Index index) {
        String indexJson = genson.serialize(index);
        return documentFactory.createFromString(indexJson);
    }
}
