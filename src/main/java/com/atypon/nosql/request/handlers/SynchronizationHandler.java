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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("syncHandler")
public class SynchronizationHandler implements DatabaseRequestHandler {

    private final Collection<String> remoteNodes;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final OkHttpClient httpClient = new OkHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<DatabaseOperation> synchronizedOperations;

    public SynchronizationHandler(
            Collection<String> remoteNodes,
            Set<DatabaseOperation> synchronizedOperations) {
        this.remoteNodes = remoteNodes;
        this.synchronizedOperations = synchronizedOperations;
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        synchronizeRequest(request);
        return DatabaseResponse.builder()
                .message("Synchronised successfully")
                .build();
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
            RequestBody requestBody = getRequestBody(requestJson);
            Request httpRequest = getHttpRequest(nodeUrl, requestBody);
            httpClient.newCall(httpRequest).execute();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RequestBody getRequestBody(String requestJson) {
        return RequestBody.create(
                MediaType.parse("application/json"),
                requestJson
        );
    }

    private Request getHttpRequest(String nodeUrl, RequestBody requestBody) {
        return new Request.Builder()
                .url(nodeUrl + "/sync")
                .post(requestBody)
                .build();
    }
}
