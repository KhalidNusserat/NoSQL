package com.atypon.nosql.store;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HashedStore implements Store {
    private final ConcurrentHashMap<String, StoredText> index;

    private final static String path = "./db/";

    private final static String filename = "unique.index";

    private final ConcurrentHashMap<String, HashSet<StoredText>> collections = new ConcurrentHashMap<>();

    private boolean exists() {
        return new File(path + filename).exists();
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<String, StoredText> read() throws IOException, ClassNotFoundException {
        if (!exists()) {
            return null;
        }
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(path + filename));
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();
        return (ConcurrentHashMap<String, StoredText>) object;
    }

    private void save() throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path + filename));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(index);
        objectOutputStream.close();
        outputStream.close();
    }

    private String alias(String collection, String id) {
        return collection + "." + id;
    }

    private void addToCollection(String collection, StoredText storedText) {
        if (!collections.containsKey(collection)) {
            collections.put(collection, new HashSet<>());
        }
        collections.get(collection).add(storedText);
    }

    private void removeFromCollection(String collection, StoredText storedText) {
        collections.get(collection).remove(storedText);
    }

    public HashedStore() throws IOException, ClassNotFoundException {
        index = Objects.requireNonNullElseGet(read(), ConcurrentHashMap::new);
    }

    @Override
    public boolean contains(String collection, String id) {
        return index.containsKey(alias(collection, id));
    }

    @Override
    public void store(String collection, String id, String content) throws Exception {
        if (index.containsKey(alias(collection, id))) {
            index.get(alias(collection, id)).delete();
            StoredText oldValue = index.get(alias(collection, id));
            StoredText newValue = oldValue.withNewContent(content);
            index.put(alias(collection, id), newValue);
            addToCollection(collection, newValue);
            removeFromCollection(collection, oldValue);
        } else {
            StoredText newValue = new StoredText(path, ".json", content);
            index.put(alias(collection, id), newValue);
            addToCollection(collection, newValue);
        }
        save();
    }

    @Override
    public String read(String collection, String id) throws IOException, AliasNotFoundException {
        if (index.containsKey(alias(collection, id))) {
            return index.get(alias(collection, id)).read();
        } else {
            throw new AliasNotFoundException("Alias not found: " + alias(collection, id));
        }
    }

    @Override
    public void remove(String collection, String id) throws AliasNotFoundException, IOException {
        if (index.containsKey(alias(collection, id))) {
            StoredText removedValue = index.get(alias(collection, id));
            index.remove(alias(collection, id));
            collections.get(collection).remove(removedValue);
            removedValue.delete();
        } else {
            throw new AliasNotFoundException("Alias not found: " + alias(collection, id));
        }
    }

    @Override
    public List<String> readCollection(String collection) throws IOException, CollectionNotFoundException {
        if (collections.containsKey(collection)) {
            List<String> result = new ArrayList<>();
            for (StoredText storedText : collections.get(collection)) {
                result.add(storedText.read());
            }
            return result;
        } else {
            throw new CollectionNotFoundException("Collection not found: " + collection);
        }
    }
}
