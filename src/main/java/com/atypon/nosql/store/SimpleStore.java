package com.atypon.nosql.store;

import java.io.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStore implements Store {
    final ConcurrentHashMap<String, StoredText> index;

    private final String path;

    final static String filename = "unique.index";

    public SimpleStore(String path) throws IOException, ClassNotFoundException {
        this.path = path;
        FilesCreator.createDirectory(path);
        index = Objects.requireNonNullElseGet(read(), ConcurrentHashMap::new);
    }

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

    @Override
    public boolean containsKey(String id) {
        return index.containsKey(id);
    }

    @Override
    public StoredText get(String id) throws ItemNotFoundException {
        if (index.containsKey(id)) {
            return index.get(id);
        } else {
            throw new ItemNotFoundException(id);
        }
    }

    @Override
    public void put(String id, String content) throws Exception {
        if (containsKey(id)) {
            index.get(id).delete();
            index.put(id, index.get(id).withNewContent(content));
        } else {
            index.put(id, new StoredText(path, ".json", content));
        }
        save();
    }

    @Override
    public void remove(String id) throws ItemNotFoundException, IOException {
        StoredText storedText = get(id);
        storedText.delete();
        index.remove(id);
    }

    @Override
    public void removeAll() throws IOException {
        for (StoredText storedText : index.values()) {
            storedText.delete();
        }
        index.clear();
        save();
    }

    @Override
    public Iterator<StoredText> iterator() {
        return index.values().iterator();
    }
}