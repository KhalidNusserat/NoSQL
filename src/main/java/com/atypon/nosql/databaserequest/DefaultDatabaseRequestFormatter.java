package com.atypon.nosql.databaserequest;

import com.atypon.nosql.database.document.IdGenerator;
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
            List<Map<String, Object>> documents = new ArrayList<>(request.payload().documents());
            documents.forEach(document -> document.put("_id", idGenerator.newId(document)));
            Payload updatedPayload = Payload.builder()
                    .setDocuments(documents)
                    .createPayload();
            return DatabaseRequest.builder()
                    .setDatabase(request.database())
                    .setCollection(request.collection())
                    .setOperation(DatabaseOperation.ADD_DOCUMENT)
                    .setPayload(updatedPayload)
                    .createDocumentRequest();
        }
        return request;
    }
}
