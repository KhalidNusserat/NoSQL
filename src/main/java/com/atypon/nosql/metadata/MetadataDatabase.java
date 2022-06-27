package com.atypon.nosql.metadata;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.users.DatabaseUser;

import java.util.List;
import java.util.Map;

public interface MetadataDatabase {

    String METADATA_DATABASE = "metadata";

    String USERS_COLLECTION = "users";

    String ROLES_COLLECTION = "roles";

    Document USERNAME_INDEX = Document.fromJson("{username: null}");

    Document USERS_SCHEMA = Document.fromMap(
            Map.of(
                    "username!", "string",
                    "password!", "string",
                    "roles!", List.of("string"),
                    "authorities!", List.of("string")
            )
    );

    Document ROLE_INDEX = Document.fromJson("{role: null}");

    Document ROLES_SCHEMA = Document.fromMap(
            Map.of(
                    "roles", List.of(
                            Map.of("role", "string",
                                    "authorities", List.of("string"))
                    )
            )
    );

    DatabaseUser.StoredDatabaseUser defaultRootAdmin = DatabaseUser.StoredDatabaseUser.builder()
            .username("admin")
            .password("admin")
            .roles(List.of("ROOT_ADMIN"))
            .authorities(List.of())
            .build();

    DatabaseUser findUser(String username);
}
