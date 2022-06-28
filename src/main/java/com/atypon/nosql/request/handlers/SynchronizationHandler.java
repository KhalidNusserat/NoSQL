package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.utils.RemoteNodeHttpClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("syncHandler")
public class SynchronizationHandler implements DatabaseRequestHandler {

    private final Collection<String> remoteNodes;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public SynchronizationHandler(Collection<String> remoteNodes) {
        this.remoteNodes = remoteNodes;
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        synchronizeRequest(request);
        return DatabaseResponse.builder()
                .message("Synchronised successfully")
                .build();
    }

    private void synchronizeRequest(DatabaseRequest request) {
        for (String nodeUrl : remoteNodes) {
            executorService.submit(() -> RemoteNodeHttpClient.execute(nodeUrl + "/exposedEndpoint", request));
        }
    }
}
