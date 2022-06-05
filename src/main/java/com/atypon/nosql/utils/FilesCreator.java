package com.atypon.nosql.utils;

import java.io.File;
import java.io.IOException;

public class FilesCreator {
    public static void createDirectory(String directory) {
        boolean ignored = new File(directory).mkdirs();
    }

    public static void create(String filepath, String filename) {
        createDirectory(filepath);
        try {
            boolean ignored = new File(filepath + filename).createNewFile();
        } catch (IOException ignored) {}
    }
}
