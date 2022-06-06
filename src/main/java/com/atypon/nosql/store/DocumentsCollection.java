package com.atypon.nosql.store;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.ObjectID;

import java.io.IOException;
import java.util.Collection;

public interface DocumentsCollection<DocumentValue> {
    boolean containsID(ObjectID id);

    Document<DocumentValue> get(ObjectID id) throws IOException;

    void put(ObjectID id, Document<DocumentValue> document) throws IOException;

    void remove(ObjectID id) throws IOException;

    Collection<Document<DocumentValue>> readAll() throws IOException;
}
