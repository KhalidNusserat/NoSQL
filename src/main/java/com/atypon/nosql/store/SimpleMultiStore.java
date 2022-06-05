package com.atypon.nosql.store;

import com.atypon.nosql.store.exceptions.CollectionNotFoundException;
import com.atypon.nosql.store.exceptions.ItemNotFoundException;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleMultiStore implements MultiStore {
    private final static String path = "./db/";

    private final ConcurrentMap<String, DocumentsCollection> collectionsIndexes = new ConcurrentHashMap<>();

    public void createNewCollection(String collection) throws IOException, ClassNotFoundException {
        collectionsIndexes.put(collection, new SimpleDocumentsCollection(path + collection + "/"));
    }

    @Override
    public boolean contains(String collection, String id) {
        return collectionsIndexes.get(collection).containsKey(id);
    }

    @Override
    public void store(String collection, String id, String content) throws Exception {
        collectionsIndexes.get(collection).put(id, content);
    }

    @Override
    public String read(String collection, String id)
            throws CollectionNotFoundException, ItemNotFoundException, IOException {
        if (collectionsIndexes.containsKey(collection)) {
            return collectionsIndexes.get(collection).get(id);
        } else {
            throw new CollectionNotFoundException(collection);
        }
    }

    @Override
    public void remove(String collection, String id) throws IOException, ItemNotFoundException {
        collectionsIndexes.get(collection).remove(id);
    }

    @Override
    public Collection<String> readCollection(String collection) throws IOException, CollectionNotFoundException {
        if (collectionsIndexes.containsKey(collection)) {
            return collectionsIndexes.get(collection).readAll();
        } else {
            throw new CollectionNotFoundException(collection);
        }
    }

    @Override
    public void removeCollection(String collection) throws CollectionNotFoundException, IOException {
        if (collectionsIndexes.containsKey(collection)) {
            collectionsIndexes.get(collection).clear();
        } else {
            throw new CollectionNotFoundException(collection);
        }
    }
}
