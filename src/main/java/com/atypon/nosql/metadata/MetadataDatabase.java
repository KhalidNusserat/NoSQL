package com.atypon.nosql.metadata;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.security.DefaultRoles;
import com.atypon.nosql.users.DatabaseUser;

import java.util.List;
import java.util.Map;

public interface MetadataDatabase {

    String METADATA_DATABASE = "metadata";

    String USERS_COLLECTION = "users";

    String ROLES_COLLECTION = "roles";

    Document USERNAME_INDEX = Document.of("username", null);

    Document USERS_SCHEMA = Document.of(
            "username!", "string",
            "password!", "string",
            "roles!", List.of("string"),
            "authorities!", List.of("string")
    );

    Document ROLE_INDEX = Document.of("role", null);

    Document ROLES_SCHEMA = Document.of(
            "roles", List.of(
                    Map.of(
                            "role", "string",
                            "authorities", List.of("string")
                    )
            )
    );

    Document defaultRootAdmin = Document.of(
            "username", "admin",
            "password", "admin",
            "roles", List.of(DefaultRoles.ROOT_ADMIN.role()),
            "authorities", List.of()
    );

    DatabaseUser findUser(String username);
}
