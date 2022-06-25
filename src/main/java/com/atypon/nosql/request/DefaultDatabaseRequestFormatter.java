package com.atypon.nosql.request;

import com.atypon.nosql.idgenerator.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DefaultDatabaseRequestFormatter implements DatabaseRequestFormatter {

    private final IdGenerator idGenerator;

    public DefaultDatabaseRequestFormatter(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public DatabaseRequest format(DatabaseRequest request) {
        if (request.operation() == DatabaseOperation.ADD_DOCUMENT) {
            return formatAddDocumentRequest(request);
        }
        return request;
    }

    private DatabaseRequest formatAddDocumentRequest(DatabaseRequest request) {
        List<Map<String, Object>> documents = new ArrayList<>(request.payload().documents());
        documents.forEach(document -> document.put("_id", idGenerator.newId(document)));
        Payload updatedPayload = Payload.builder()
                .setDocuments(documents)
                .createPayload();
        return DatabaseRequest.builder()
                .database(request.database())
                .collection(request.collection())
                .operation(DatabaseOperation.ADD_DOCUMENT)
                .payload(updatedPayload)
                .build();
    }
}
