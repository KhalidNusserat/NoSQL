package com.atypon.nosql.synchronisation;

import org.springframework.http.HttpMethod;

import java.util.Map;

public interface SynchronisationService {

    DefaultSynchronisationService requestBody(Map<String, Object> requestBody);

    DefaultSynchronisationService method(HttpMethod method);

    DefaultSynchronisationService url(String url);

    DefaultSynchronisationService parameters(String... urlParameters);

    void synchronise();
}
