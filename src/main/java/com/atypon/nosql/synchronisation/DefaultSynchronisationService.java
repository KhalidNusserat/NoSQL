package com.atypon.nosql.synchronisation;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Map;

@Service
public class DefaultSynchronisationService implements SynchronisationService {
    private final Collection<String> secondaryNodesUrls;

    private final RestTemplate restTemplate = new RestTemplate();

    private Map<String, Object> requestBody;

    private HttpMethod method;

    private String resourceUrl;

    private Object[] urlVariables;

    public DefaultSynchronisationService(Collection<String> secondaryNodesUrls) {
        this.secondaryNodesUrls = secondaryNodesUrls;
    }

    @Override
    public DefaultSynchronisationService requestBody(Map<String, Object> requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    @Override
    public DefaultSynchronisationService method(HttpMethod method) {
        this.method = method;
        return this;
    }

    @Override
    public DefaultSynchronisationService url(String url) {
        this.resourceUrl = url;
        return this;
    }

    @Override
    public DefaultSynchronisationService parameters(String... urlParameters) {
        this.urlVariables = urlParameters;
        return this;
    }

    @Override
    public void synchronise() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        for (String secondaryNodeUrl : secondaryNodesUrls) {
            if (method == HttpMethod.GET) {
                restTemplate.getForObject(secondaryNodeUrl + resourceUrl, Void.class, urlVariables);
            } else if (method == HttpMethod.POST) {
                restTemplate.postForObject(secondaryNodeUrl + resourceUrl, requestBody, Void.class, urlVariables);
            } else if (method == HttpMethod.PUT) {
                restTemplate.put(secondaryNodeUrl + resourceUrl, requestBody, Void.class, urlVariables);
            } else if (method == HttpMethod.DELETE) {
                restTemplate.delete(secondaryNodeUrl + resourceUrl, urlVariables);
            }
        }
        requestBody = null;
        urlVariables = new Object[0];
    }
}
