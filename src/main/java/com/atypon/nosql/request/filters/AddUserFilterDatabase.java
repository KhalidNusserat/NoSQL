package com.atypon.nosql.request.filters;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.metadata.MetadataDatabase;
import com.atypon.nosql.request.BasicDatabaseRequestScope;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.DatabaseRequestScope;
import com.atypon.nosql.request.Payload;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddUserFilterDatabase extends DatabaseRequestFilter {

    private final PasswordEncoder passwordEncoder;

    private final DatabaseRequestScope requestScope = BasicDatabaseRequestScope.builder()
            .databaseRegex(MetadataDatabase.METADATA_DATABASE)
            .collectionRegex(MetadataDatabase.USERS_COLLECTION)
            .build();

    public AddUserFilterDatabase(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public DatabaseRequest applyOn(DatabaseRequest request) {
        if (request.isWithinScope(requestScope)) {
            return DatabaseRequest.builder()
                    .database(request.database())
                    .collection(request.collection())
                    .operation(request.operation())
                    .payload(encryptPassword(request.payload()))
                    .build();
        } else {
            return request;
        }
    }

    private Payload encryptPassword(Payload oldPayload) {
        Document document = oldPayload.documents().get(0);
        String plainPassword = (String) document.toMap().get("password");
        String encodedPassword = passwordEncoder.encode(plainPassword);
        document = document.overrideFields(Document.of("password", encodedPassword));
        return Payload.builder()
                .criteria(oldPayload.criteria())
                .documents(List.of(document))
                .build();
    }
}
