package com.atypon.nosql.synchronisation;

import org.springframework.http.HttpMethod;

import java.util.Map;

public interface SynchronisationService {

    SynchronisationService newInstance();

    SynchronisationService requestBody(Map<String, Object> requestBody);

    SynchronisationService method(HttpMethod method);

    SynchronisationService url(String url);

    SynchronisationService parameters(String... urlParameters);

    void synchronise();
}
