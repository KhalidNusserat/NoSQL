package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SynchronizationHandler implements DatabaseRequestHandler {

    private final Collection<String> remoteNodes;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final RestTemplate restTemplate = new RestTemplate();

    private final HttpHeaders headers = new HttpHeaders();

    public SynchronizationHandler(Collection<String> remoteNodes) {
        this.remoteNodes = remoteNodes;
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        for (String nodeUrl : remoteNodes) {
            synchroniseNode(nodeUrl, request);
        }
        return DatabaseResponse.builder()
                .message("Synchronised successfully")
                .build();
    }

    private void synchroniseNode(String nodeUrl, DatabaseRequest request) {
        HttpEntity<DatabaseRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.postForObject(nodeUrl + "/sync", entity, Void.class);
    }
}
