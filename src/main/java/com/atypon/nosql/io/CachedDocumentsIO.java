package com.atypon.nosql.io;

import com.atypon.nosql.cache.Cache;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.utils.ExtraFileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CachedDocumentsIO<T extends Document<?>> implements DocumentsIO<T> {
    private final DocumentsIO<T> documentsIO;

    private final Cache<Path, T> cache;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public CachedDocumentsIO(DocumentsIO<T> documentsIO, Cache<Path, T> cache) {
        this.documentsIO = documentsIO;
        this.cache = cache;
    }

    public static <T extends Document<?>> CachedDocumentsIO<T> from(DocumentsIO<T> documentsIO, Cache<Path, T> cache) {
        return new CachedDocumentsIO<>(documentsIO, cache);
    }

    @Override
    public Path write(T document, Path directory) throws IOException {
        Path filepath = documentsIO.write(document, directory);
        cache.put(filepath, document);
        return filepath;
    }

    @Override
    public Optional<T> read(Path documentPath) {
        Optional<T> cacheResult = cache.get(documentPath);
        if (cacheResult.isPresent()) {
            return cacheResult;
        } else {
            return documentsIO.read(documentPath);
        }
    }

    @Override
    public void delete(Path documentPath) {
        lock.writeLock().lock();
        cache.remove(documentPath);
        documentsIO.delete(documentPath);
        lock.writeLock().unlock();
    }

    @Override
    public Path update(T newDocument, Path documentPath) throws IOException {
        lock.writeLock().lock();
        Path filepath = documentsIO.update(newDocument, documentPath);
        cache.put(filepath, newDocument);
        lock.writeLock().unlock();
        return filepath;
    }

    @Override
    public Collection<T> readDirectory(Path directoryPath) {
        lock.readLock().lock();
        try {
            return Files.walk(directoryPath, 1)
                    .filter(ExtraFileUtils::isJsonFile)
                    .map(path -> cache.get(path).orElse(documentsIO.read(path).orElseThrow()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Interrupted while reading all documents in the directory: " + directoryPath);
        } finally {
            lock.readLock().unlock();
        }
    }
}
