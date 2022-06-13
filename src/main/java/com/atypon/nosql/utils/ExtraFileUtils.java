package com.atypon.nosql.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtraFileUtils {
    private static final PathMatcher jsonMatcher = FileSystems.getDefault()
            .getPathMatcher("glob:**/*.json");

    private static final PathMatcher indexMatcher = FileSystems.getDefault()
            .getPathMatcher("glob:**/*.index");

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

    public static Set<Path> getDirectoryContent(Path directoryPath) throws IOException {
        return Files.list(directoryPath)
                .collect(Collectors.toSet());
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
}
