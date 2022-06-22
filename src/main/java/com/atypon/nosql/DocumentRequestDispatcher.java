package com.atypon.nosql;

import java.util.ArrayList;
import java.util.Collection;

public class DocumentRequestDispatcher implements DocumentRequestHandler {
    private final Collection<DocumentRequestHandler> requestHandlers = new ArrayList<>();

    @Override
    public void handle(DocumentRequest request) {
        requestHandlers.forEach(requestHandler -> requestHandler.handle(request));
    }

    public void addHandler(DocumentRequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
    }
}
