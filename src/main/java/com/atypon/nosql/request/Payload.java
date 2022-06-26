package com.atypon.nosql.request;

import com.atypon.nosql.document.Document;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public record Payload(Document criteria,
                      List<Document> documents,
                      Document index,
                      boolean uniqueIndex,
                      Document update,
                      Document schema) {

    @Builder
    public Payload {}
}
