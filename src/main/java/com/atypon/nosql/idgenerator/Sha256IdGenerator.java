package com.atypon.nosql.idgenerator;

import com.google.common.hash.Hashing;
import lombok.SneakyThrows;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@ToString
@Component
public class Sha256IdGenerator implements IdGenerator {

    @SneakyThrows
    @Override
    public String newId(Object object) {
        String time = Long.toString(System.currentTimeMillis());
        return Hashing.sha256()
                .hashString(object.toString() + time, StandardCharsets.UTF_8)
                .toString();
    }
}
