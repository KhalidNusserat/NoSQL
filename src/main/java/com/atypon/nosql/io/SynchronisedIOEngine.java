package com.atypon.nosql.io;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.document.DocumentGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
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
    public Path write(Document<?> document, Path directory) throws IOException {
        lock.writeLock().lock();
        Path documentPath = ioEngine.write(document, directory);
        lock.readLock().unlock();
        return documentPath;
    }

    @Override
    public <T extends Document<?>> Optional<T> read(Path documentPath, DocumentGenerator<T> documentGenerator) {
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
    public Path update(Document<?> updatedDocument, Path documentPath) throws IOException {
        lock.writeLock().lock();
        Path updatedPath = ioEngine.update(updatedDocument, documentPath);
        lock.writeLock().unlock();
        return updatedPath;
    }

    @Override
    public <T extends Document<?>> Collection<T> readDirectory(Path directoryPath, DocumentGenerator<T> documentGenerator) {
        lock.readLock().lock();
        Collection<T> result = ioEngine.readDirectory(directoryPath, documentGenerator);
        lock.readLock().unlock();
        return result;
    }
}
