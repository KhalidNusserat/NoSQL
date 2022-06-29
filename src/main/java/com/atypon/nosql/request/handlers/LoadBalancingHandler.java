package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.utils.RemoteNodeHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component("loadBalancingHandler")
public class LoadBalancingHandler implements DatabaseRequestHandler {

    private final List<String> remoteNodesUrls;

    private final DatabaseRequestHandler operationsHandler;

    private final Random random = new Random();

    public LoadBalancingHandler(
            List<String> remoteNodesUrls,
            @Qualifier("operationsHandler") DatabaseRequestHandler operationsHandler) {
        this.remoteNodesUrls = remoteNodesUrls;
        this.operationsHandler = operationsHandler;
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        if (remoteNodesUrls != null) {
            String selectedRemoteNodeUrl = remoteNodesUrls.get(random.nextInt(remoteNodesUrls.size()));
            return RemoteNodeHttpClient.execute(selectedRemoteNodeUrl + "/exposedEndpoint", request);
        } else {
            return operationsHandler.handle(request);
        }
    }
}
