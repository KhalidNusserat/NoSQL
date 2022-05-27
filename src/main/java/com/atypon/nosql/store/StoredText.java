package com.atypon.nosql.store;

import java.io.*;
import java.util.Random;

public class StoredText implements Serializable {
    @Serial
    private static final long serialVersionUID = -3262126900798120778L;

    private final String filename;

    private final String filepath;

    private final String extension;

    private void create() throws IOException {
        if (new File(filepath).mkdirs() || new File(filepath).exists()) {
            if (!new File(getFilePath()).createNewFile()) {
                throw new FileNotStoredException("Could not store the file: " + filename);
            }
        } else {
            throw new FileNotStoredException("Couldn't create the directory: " + filepath);
        }
    }

    private void store(String content) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath()));
        writer.write(content);
        writer.close();
    }

    public StoredText(String filepath, String extension, String content) throws Exception {
        this.filepath = filepath;
        this.extension = extension;
        this.filename = Long.toString(new Random().nextLong());
        create();
        store(content);
    }

    public String getFilePath() {
        return filepath + filename + extension;
    }

    public boolean exists() {
        return new File(getFilePath()).exists();
    }

    public void delete() throws Exception {
        synchronized (filename) {
            if (exists()) {
                File file = new File(getFilePath());
                if (!file.delete()) {
                    throw new Exception("File not deleted successfully: " + getFilePath());
                }
            }
        }
    }

    public String read() throws IOException {
        synchronized (filename) {
            BufferedReader reader = new BufferedReader(new FileReader(getFilePath()));
            String content = reader.lines().reduce((a, b) -> a + b).orElseThrow();
            reader.close();
            return content;
        }
    }

    public StoredText withNewContent(String content) throws Exception {
        return new StoredText(filepath, extension, content);
    }

    @Override
    public String toString() {
        return "SimpleStoredContent{" +
                getFilePath() +
                '}';
    }
}
