package com.atypon.nosql.request;

import com.atypon.nosql.document.Document;
import lombok.Builder;

import java.util.List;

public record Payload(Document criteria,
                      List<Document> documents,
                      Document index,
                      boolean uniqueIndex,
                      Document update,
                      Document schema) {

    @Builder
    public Payload {
    }
}
