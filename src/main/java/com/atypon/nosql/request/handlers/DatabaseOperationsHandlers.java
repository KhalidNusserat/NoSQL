package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.annotations.DatabaseOperationMapping;
import com.atypon.nosql.response.DatabaseResponse;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@ToString
@Component
public class DatabaseOperationsHandlers extends OperationsHandlers {

    @DatabaseOperationMapping(DatabaseOperation.CREATE_DATABASE)
    public DatabaseResponse createDatabase(DatabaseRequest request) {
        databasesManager.createDatabase(request.database());
        return DatabaseResponse.builder()
                .message(String.format("Created the database <%s>", request.database()))
                .build();
    }

    @DatabaseOperationMapping(DatabaseOperation.GET_DATABASES)
    public DatabaseResponse getDatabases(DatabaseRequest request) {
        Collection<String> databases = databasesManager.getDatabasesNames();
        Collection<Map<String, Object>> formattedResult = formatDatabasesNames();
        return DatabaseResponse.builder()
                .message("Found [" + databases.size() + "] databases")
                .result(formattedResult)
                .build();
    }

    private List<Map<String, Object>> formatDatabasesNames() {
        return databasesManager.getDatabasesNames().stream()
                .map(databaseName -> Map.of("database", (Object) databaseName))
                .toList();
    }

    @DatabaseOperationMapping(DatabaseOperation.REMOVE_DATABASE)
    public DatabaseResponse removeDatabase(DatabaseRequest request) {
        databasesManager.removeDatabase(request.database());
        return DatabaseResponse.builder()
                .message(String.format("Removed the database \"%s\"", request.database()))
                .build();
    }
}
