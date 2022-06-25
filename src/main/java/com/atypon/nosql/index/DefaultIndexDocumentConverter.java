package com.atypon.nosql.index;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentFactory;
import com.atypon.nosql.gsondocument.GsonDocument;
import com.atypon.nosql.utils.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefaultIndexDocumentConverter implements IndexDocumentConverter {

    private final Gson gson;

    private final DocumentFactory documentFactory;

    public DefaultIndexDocumentConverter(DocumentFactory documentFactory) {
        RuntimeTypeAdapterFactory<Document> adapter = RuntimeTypeAdapterFactory
                .of(Document.class, "document")
                .registerSubtype(GsonDocument.class, "gsonDocument");
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(adapter)
                .serializeNulls()
                .create();
        this.documentFactory = documentFactory;
    }

    @Override
    public Index toIndex(Document indexDocument) {
        return gson.fromJson(indexDocument.toString(), DefaultIndex.class);
    }

    @Override
    public Document toDocument(Index index) {
        String indexJson = gson.toJson(index);
        return documentFactory.createFromString(indexJson);
    }
}
