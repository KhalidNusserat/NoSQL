package com.atypon.nosql.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtraFileUtils {
    public static boolean isJsonFile(Path path) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json");
        return matcher.matches(path);
    }

    public static Set<Path> getDirectoryContent(Path directoryPath) throws IOException {
        return Files.list(directoryPath)
                .collect(Collectors.toSet());
    }
}
