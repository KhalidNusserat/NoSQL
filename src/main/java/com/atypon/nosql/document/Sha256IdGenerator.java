package com.atypon.nosql.document;

import com.google.common.hash.Hashing;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Sha256IdGenerator implements IdGenerator {
    @SneakyThrows
    @Override
    public String newId(Object object) {
        return Hashing.sha256()
                .hashString(object.toString(), StandardCharsets.UTF_8)
                .toString();
    }
}
