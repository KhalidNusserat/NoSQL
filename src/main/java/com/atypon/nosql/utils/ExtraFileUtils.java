package com.atypon.nosql.utils;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class ExtraFileUtils {
    public static boolean isJsonFile(Path path) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json");
        return matcher.matches(path);
    }
}
