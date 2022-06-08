package com.atypon.nosql.collection;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.io.DocumentsIO;
import com.atypon.nosql.utils.ExtraFileUtils;
import com.google.common.base.Preconditions;

import javax.naming.directory.SchemaViolationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultDocumentsCollection<E, T extends Document<E>> implements DocumentsCollection<T> {
    private final DocumentsIO<T> documentsIO;

    private final Path directoryPath;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public DefaultDocumentsCollection(DocumentsIO<T> documentsIO, Path directoryPath) {
        this.documentsIO = documentsIO;
        this.directoryPath = directoryPath;
    }

    public static <E, T extends Document<E>> DefaultDocumentsCollection<E, T> from(
            DocumentsIO<T> documentsIO, Path directoryPath) {
        return new DefaultDocumentsCollection<>(documentsIO, directoryPath);
    }

    @Override
    public boolean contains(T matchDocument) {
        lock.readLock().lock();
        try {
            return Files.walk(directoryPath)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(documentsIO::read)
                    .filter(Optional::isPresent)
                    .anyMatch(document -> document.get().matches(matchDocument));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't access the directory: " + directoryPath);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Collection<T> getAllThatMatches(T matchDocument) {
        lock.readLock().lock();
        try {
            return Files.walk(directoryPath)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(documentsIO::read)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(document -> document.matches(matchDocument))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't access the directory: " + directoryPath);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Collection<T> getAll() {
        return documentsIO.readAll(directoryPath);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Path put(T document) throws IOException, SchemaViolationException {
        List<Path> paths = getPathsThatMatch((T) document.matchID());
        if (paths.size() == 1) {
            return documentsIO.update(document, paths.get(0));
        } else if (paths.size() == 0) {
            return documentsIO.write(document, directoryPath);
        } else {
            throw new IllegalStateException("There are multiple files with the same ObjectID");
        }
    }

    @Override
    public void remove(T matchDocument) throws IOException {
        for (Path path : getPathsThatMatch(matchDocument)) {
            documentsIO.delete(path);
        }
    }

    private List<Path> getPathsThatMatch(T matchDocument) {
        lock.readLock().lock();
        try {
            return Files.walk(directoryPath)
                    .filter(ExtraFileUtils::isJsonFile)
                    .filter(path -> documentsIO.read(path)
                            .map(document -> document.matches(matchDocument))
                            .orElse(false))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't access the directory: " + directoryPath);
        } finally {
            lock.readLock().unlock();
        }
    }
}