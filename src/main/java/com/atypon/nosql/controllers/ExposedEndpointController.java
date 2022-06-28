package com.atypon.nosql.controllers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.handlers.DatabaseRequestHandler;
import com.atypon.nosql.response.DatabaseResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExposedEndpointController {

    private final DatabaseRequestHandler operationsHandler;

    public ExposedEndpointController(@Qualifier("operationsHandler") DatabaseRequestHandler operationsHandler) {
        this.operationsHandler = operationsHandler;
    }

    @PostMapping("/exposedEndpoint")
    private DatabaseResponse synchronise(@RequestBody DatabaseRequest request) {
        return operationsHandler.handle(request);
    }
}
