package com.atypon.nosql.metadata;

import com.atypon.nosql.document.Document;
import com.atypon.nosql.security.DefaultRoles;

import java.util.List;

public class MetadataDatabaseConstants {

    public static final String METADATA_DATABASE = "metadata";

    public static final String USERS_COLLECTION = "users";

    public static final String ROLES_COLLECTION = "roles";

    public static final Document USERNAME_INDEX = Document.of("username", null);

    public static final Document USERS_SCHEMA = Document.of(
            "username!", "string",
            "password!", "string",
            "roles!", List.of("string"),
            "authorities!", List.of("string")
    );

    public static final Document ROLE_INDEX = Document.of("role", null);

    public static final Document ROLES_SCHEMA = Document.of(
            "role!", "string",
            "authorities!", List.of("string")
    );

    public static final Document defaultAdmin = Document.of(
            "username", "admin",
            "password", "admin",
            "roles", List.of(DefaultRoles.ADMIN.role()),
            "authorities", List.of()
    );

    private MetadataDatabaseConstants() {
    }
}
