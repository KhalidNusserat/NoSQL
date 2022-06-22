package com.atypon.nosql;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SynchronizationHandler implements DocumentRequestHandler {

    private final Collection<RemoteNode> remoteNodes;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final RestTemplate restTemplate = new RestTemplate();

    public SynchronizationHandler(Collection<RemoteNode> remoteNodes) {
        this.remoteNodes = remoteNodes;
    }

    @Override
    public void handle(DocumentRequest request) {
        for (RemoteNode remoteNode : remoteNodes) {
            executorService.submit(() -> synchroniseNode(remoteNode, request));
        }
    }

    private void synchroniseNode(RemoteNode remoteNode, DocumentRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("sync", remoteNode.syncPassword());
        HttpEntity<DocumentRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.postForObject(remoteNode.url() + "/sync", entity, Void.class);
    }
}
