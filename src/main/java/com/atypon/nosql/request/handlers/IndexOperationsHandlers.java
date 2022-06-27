package com.atypon.nosql.request.handlers;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.request.annotations.DatabaseOperationMapping;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class IndexOperationsHandlers extends OperationsHandlers {

    @DatabaseOperationMapping(DatabaseOperation.GET_INDEXES)
    public DatabaseResponse getIndexes(DatabaseRequest request) {
        Collection<Map<String, Object>> indexes = databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .getIndexes()
                .stream()
                .map(Document::toMap)
                .toList();
        return DatabaseResponse.builder()
                .message("Found [" + indexes.size() + "] indexes")
                .result(indexes)
                .build();
    }

    @DatabaseOperationMapping(DatabaseOperation.CREATE_INDEX)
    public DatabaseResponse createIndex(DatabaseRequest request) {
        Payload payload = request.payload();
        databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .createIndex(
                        payload.index(),
                        payload.uniqueIndex()
                );
        synchronizationHandler.handle(request);
        String message = String.format(
                "Created new index for the collection <%s/%s>",
                request.database(),
                request.collection()
        );
        return DatabaseResponse.builder()
                .message(message)
                .build();
    }

    @DatabaseOperationMapping(DatabaseOperation.REMOVE_INDEX)
    public DatabaseResponse removeIndex(DatabaseRequest request) {
        Payload payload = request.payload();
        databasesManager.getDatabase(request.database())
                .getCollection(request.collection())
                .removeIndex(payload.index());
        String message = String.format(
                "Removed an index from the collection <%s/%s>",
                request.database(),
                request.collection()
        );
        synchronizationHandler.handle(request);
        return DatabaseResponse.builder()
                .message(message)
                .build();
    }
}
