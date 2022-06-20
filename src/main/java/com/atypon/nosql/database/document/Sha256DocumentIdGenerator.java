package com.atypon.nosql.database.document;

import com.google.common.hash.Hashing;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Sha256DocumentIdGenerator implements DocumentIdGenerator {
    @SneakyThrows
    @Override
    public String newId(Document document) {
        return Hashing.sha256()
                .hashString(document.toString(), StandardCharsets.UTF_8)
                .toString();
    }
}
