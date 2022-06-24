package com.atypon.nosql.controllers;

import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.requesthandlers.DatabaseRequestHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SynchronisationController {

    private final DatabaseRequestHandler requestHandler;

    public SynchronisationController(@Qualifier("defaultHandler") DatabaseRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @PostMapping("/sync")
    private void synchronise(@RequestBody DatabaseRequest request) {
        requestHandler.handle(request);
    }
}
