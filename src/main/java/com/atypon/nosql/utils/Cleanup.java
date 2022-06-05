package com.atypon.nosql.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class Cleanup {
    public static void cleanupDirectory(@NotNull File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                boolean ignored = file.delete();
            } else {
                cleanupDirectory(file);
            }
        }
        boolean ignored = directory.delete();
    }
}
