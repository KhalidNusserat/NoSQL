package com.atypon.nosql.utils;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.response.DatabaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.io.UncheckedIOException;

public class RemoteNodeHttpClient {

    private static final RemoteNodeHttpClient INSTANCE = new RemoteNodeHttpClient();

    final OkHttpClient httpClient = new OkHttpClient();

    final ObjectMapper objectMapper = new ObjectMapper();

    private RemoteNodeHttpClient() {
    }

    public static DatabaseResponse execute(String url, DatabaseRequest request) {
        return INSTANCE.executeRequest(url, request);
    }

    private DatabaseResponse executeRequest(String url, DatabaseRequest request) {
        try {
            String requestJson = objectMapper.writeValueAsString(request);
            RequestBody requestBody = getRequestBody(requestJson);
            Request httpRequest = getHttpRequest(url, requestBody);
            String responseJson = httpClient.newCall(httpRequest).execute().body().string();
            return objectMapper.readValue(responseJson, DatabaseResponse.class);
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

    private Request getHttpRequest(String url, RequestBody requestBody) {
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }
}