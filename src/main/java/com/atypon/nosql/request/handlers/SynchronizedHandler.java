package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("defaultHandler")
public class SynchronizedHandler implements DatabaseRequestHandler {

    private final Collection<String> remoteNodes;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final OkHttpClient httpClient = new OkHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DatabaseRequestHandler requestHandler;

    private final Set<DatabaseOperation> synchronizedOperations;

    public SynchronizedHandler(
            Collection<String> remoteNodes,
            @Qualifier("operationsHandler") DatabaseRequestHandler requestHandler,
            Set<DatabaseOperation> synchronizedOperations) {
        this.remoteNodes = remoteNodes;
        this.requestHandler = requestHandler;
        this.synchronizedOperations = synchronizedOperations;
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        DatabaseResponse response = requestHandler.handle(request);
        synchronizeRequest(request);
        return response;
    }

    private void synchronizeRequest(DatabaseRequest request) {
        if (synchronizedOperations.contains(request.operation())) {
            for (String nodeUrl : remoteNodes) {
                executorService.submit(() -> synchroniseNode(nodeUrl, request));
            }
        }
    }

    private void synchroniseNode(String nodeUrl, DatabaseRequest request) {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    requestJson
            );
            Request httpRequest = new Request.Builder()
                    .url(nodeUrl + "/sync")
                    .post(requestBody)
                    .build();
            httpClient.newCall(httpRequest).execute();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
