package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.request.annotations.DatabaseOperationMapping;
import com.atypon.nosql.response.DatabaseResponse;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@ToString
@Component
public class CollectionOperationsHandlers extends OperationsHandlers {

    @DatabaseOperationMapping(DatabaseOperation.CREATE_COLLECTION)
    public DatabaseResponse createCollection(DatabaseRequest request) {
        Payload payload = request.payload();
        databasesManager.getDatabase(request.database())
                .createCollection(request.collection(), payload.schema());
        return DatabaseResponse.builder()
                .message(String.format("Created the collection <%s/%s>", request.database(), request.collection()))
                .build();
    }

    @DatabaseOperationMapping(DatabaseOperation.GET_COLLECTIONS)
    public DatabaseResponse getCollections(DatabaseRequest request) {
        Collection<String> collections = databasesManager.getDatabase(request.database()).getCollectionsNames();
        Collection<Map<String, Object>> formattedResult = formatCollectionsNames(collections);
        return DatabaseResponse.builder()
                .message("Found [" + collections.size() + "] collections")
                .result(formattedResult)
                .build();
    }

    private Collection<Map<String, Object>> formatCollectionsNames(Collection<String> result) {
        return result.stream()
                .map(databaseName -> Map.of("collection", (Object) databaseName))
                .toList();
    }

    @DatabaseOperationMapping(DatabaseOperation.REMOVE_COLLECTION)
    public DatabaseResponse removeCollection(DatabaseRequest request) {
        databasesManager.getDatabase(request.database())
                .removeCollection(request.collection());
        return DatabaseResponse.builder()
                .message(String.format("Removed the collection \"%s/%s\"", request.database(), request.collection()))
                .build();
    }
}
