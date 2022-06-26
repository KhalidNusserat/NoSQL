package com.atypon.nosql.idgenerator;

import com.atypon.nosql.document.Document;
import com.google.common.hash.Hashing;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Sha256IdGenerator implements IdGenerator {

    public Sha256IdGenerator() {
        Document.setIdGenerator(this);
    }

    @SneakyThrows
    @Override
    public String newId(Object object) {
        String time = Long.toString(System.currentTimeMillis());
        return Hashing.sha256()
                .hashString(object.toString() + time, StandardCharsets.UTF_8)
                .toString();
    }
}
