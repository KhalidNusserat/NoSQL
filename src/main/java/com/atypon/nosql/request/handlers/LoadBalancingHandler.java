package com.atypon.nosql.request.handlers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import com.atypon.nosql.utils.RemoteNodeHttpClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component("loadBalancingHandler")
public class LoadBalancingHandler implements DatabaseRequestHandler {

    private final List<String> remoteNodesUrls;

    private final Random random = new Random();

    public LoadBalancingHandler(List<String> remoteNodesUrls) {
        this.remoteNodesUrls = remoteNodesUrls;
    }

    @Override
    public DatabaseResponse handle(DatabaseRequest request) {
        String selectedRemoteNodeUrl = remoteNodesUrls.get(random.nextInt(remoteNodesUrls.size()));
        return RemoteNodeHttpClient.execute(selectedRemoteNodeUrl + "/exposedEndpoint", request);
    }
}
