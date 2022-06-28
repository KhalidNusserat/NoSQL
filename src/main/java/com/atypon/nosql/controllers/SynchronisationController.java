package com.atypon.nosql.controllers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.handlers.DatabaseRequestHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SynchronisationController {

    private final DatabaseRequestHandler operationsHandler;

    public SynchronisationController(@Qualifier("operationsHandler") DatabaseRequestHandler operationsHandler) {
        this.operationsHandler = operationsHandler;
    }

    @PostMapping("/sync")
    private void synchronise(@RequestBody DatabaseRequest request) {
        operationsHandler.handle(request);
    }
}
