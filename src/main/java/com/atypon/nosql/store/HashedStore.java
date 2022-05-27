package com.atypon.nosql.store;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HashedStore implements Store {
    private final ConcurrentHashMap<String, StoredText> index;

    private final static String path = "./db/";

    private final static String filename = "unique.index";

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

    public HashedStore() throws IOException, ClassNotFoundException {
        index = Objects.requireNonNullElseGet(read(), ConcurrentHashMap::new);
    }

    @Override
    public boolean containsAlias(String collection, String id) {
        return index.containsKey(alias(collection, id));
    }

    @Override
    public void store(String collection, String id, String content) throws Exception {
        if (index.containsKey(alias(collection, id))) {
            StoredText storedContent = index.get(alias(collection, id));
            index.put(alias(collection, id), storedContent.withNewContent(content));
            storedContent.delete();
        } else {
            index.put(alias(collection, id), new StoredText(path, ".json", content));
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
}
