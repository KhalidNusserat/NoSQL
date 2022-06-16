package com.atypon.nosql.database.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;

public class FileUtils {
    private static final PathMatcher jsonMatcher = FileSystems.getDefault()
            .getPathMatcher("glob:**/*.json");

    public static boolean isJsonFile(Path path) {
        return jsonMatcher.matches(path);
    }

    public static void deleteDirectory(Path directory) {
        try {
            Files.walk(directory)
                    .forEach(path -> {
                        if (!path.equals(directory) && Files.isDirectory(path)) {
                            deleteDirectory(path);
                        } else if (Files.isRegularFile(path)) {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
            Files.delete(directory);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static int countFiles(Path directoryPath, int level) {
        try {
            return Files.walk(directoryPath, level)
                    .map(path -> {
                        if (Files.isRegularFile(path)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    })
                    .reduce(Integer::sum)
                    .orElse(0);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static Stream<Path> traverseDirectory(Path directory) {
        try {
            return Files.walk(directory, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDirectories(Path databasesDirectory) {
        try {
            Files.createDirectories(databasesDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
