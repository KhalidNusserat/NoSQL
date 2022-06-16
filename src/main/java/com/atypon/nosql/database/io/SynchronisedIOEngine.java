package com.atypon.nosql.database.io;

import com.atypon.nosql.database.document.Document;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronisedIOEngine implements IOEngine {
    private final IOEngine ioEngine;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private SynchronisedIOEngine(IOEngine ioEngine) {
        this.ioEngine = ioEngine;
    }

    public static SynchronisedIOEngine from(IOEngine ioEngine) {
        return new SynchronisedIOEngine(ioEngine);
    }

    @Override
    public Path write(Document document, Path directory) {
        lock.writeLock().lock();
        Path documentPath = ioEngine.write(document, directory);
        lock.readLock().unlock();
        return documentPath;
    }

    @Override
    public Optional<Document> read(Path documentPath) {
        lock.readLock().lock();
        ioEngine.read(documentPath);
        lock.readLock().unlock();
        return Optional.empty();
    }

    @Override
    public void delete(Path documentPath) {
        lock.writeLock().lock();
        ioEngine.delete(documentPath);
        lock.writeLock().unlock();
    }

    @Override
    public Path update(Document updatedDocument, Path documentPath) {
        lock.writeLock().lock();
        Path updatedPath = ioEngine.update(updatedDocument, documentPath);
        lock.writeLock().unlock();
        return updatedPath;
    }

    @Override
    public List<Document> readDirectory(Path directoryPath) {
        lock.readLock().lock();
        List<Document> result = ioEngine.readDirectory(directoryPath);
        lock.readLock().unlock();
        return result;
    }
}
