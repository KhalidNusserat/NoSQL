package com.atypon.nosql.database.io;

import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronisedIOEngine<T extends Document<?>> implements IOEngine<T> {
    private final IOEngine<T> ioEngine;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private SynchronisedIOEngine(IOEngine<T> ioEngine) {
        this.ioEngine = ioEngine;
    }

    public static <T extends Document<?>> SynchronisedIOEngine<T> from(IOEngine<T> ioEngine) {
        return new SynchronisedIOEngine<>(ioEngine);
    }

    @Override
    public Path write(T document, Path directory) {
        lock.writeLock().lock();
        Path documentPath = ioEngine.write(document, directory);
        lock.readLock().unlock();
        return documentPath;
    }

    @Override
    public Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator) {
        lock.readLock().lock();
        Optional<T> result = ioEngine.read(documentPath, documentGenerator);
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
    public Path update(T updatedDocument, Path documentPath) {
        lock.writeLock().lock();
        Path updatedPath = ioEngine.update(updatedDocument, documentPath);
        lock.writeLock().unlock();
        return updatedPath;
    }

    @Override
    public List<T> readDirectory(Path directoryPath, DocumentGenerator<T> documentGenerator) {
        lock.readLock().lock();
        List<T> result = ioEngine.readDirectory(directoryPath, documentGenerator);
        lock.readLock().unlock();
        return result;
    }
}
