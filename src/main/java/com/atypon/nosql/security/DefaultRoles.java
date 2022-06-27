package com.atypon.nosql.security;

import com.atypon.nosql.security.DatabaseRole.StoredDatabaseRole;

import java.util.List;

public interface DefaultRoles {

    StoredDatabaseRole READER = StoredDatabaseRole.builder()
            .role("READER")
            .authorities(List.of(DatabaseAuthority.READ_DOCUMENTS))
            .build();

    StoredDatabaseRole USER = StoredDatabaseRole.builder()
            .role("USER")
            .authorities(List.of(
                    DatabaseAuthority.READ_DOCUMENTS,
                    DatabaseAuthority.ADD_DOCUMENTS,
                    DatabaseAuthority.UPDATE_DOCUMENTS,
                    DatabaseAuthority.REMOVE_DOCUMENTS)
            )
            .build();

    StoredDatabaseRole OWNER = StoredDatabaseRole.builder()
            .role("OWNER")
            .authorities(List.of(
                    DatabaseAuthority.READ_DOCUMENTS,
                    DatabaseAuthority.ADD_DOCUMENTS,
                    DatabaseAuthority.UPDATE_DOCUMENTS,
                    DatabaseAuthority.REMOVE_DOCUMENTS,
                    DatabaseAuthority.GET_INDEXES,
                    DatabaseAuthority.CREATE_INDEX,
                    DatabaseAuthority.REMOVE_INDEX)
            )
            .build();

    StoredDatabaseRole MANAGER = StoredDatabaseRole.builder()
            .role("MANAGER")
            .authorities(List.of(
                    DatabaseAuthority.READ_DOCUMENTS,
                    DatabaseAuthority.ADD_DOCUMENTS,
                    DatabaseAuthority.UPDATE_DOCUMENTS,
                    DatabaseAuthority.REMOVE_DOCUMENTS,
                    DatabaseAuthority.GET_INDEXES,
                    DatabaseAuthority.CREATE_INDEX,
                    DatabaseAuthority.REMOVE_INDEX,
                    DatabaseAuthority.GET_COLLECTIONS,
                    DatabaseAuthority.CREATE_COLLECTION,
                    DatabaseAuthority.REMOVE_COLLECTION)
            )
            .build();

    StoredDatabaseRole ADMIN = StoredDatabaseRole.builder()
            .role("ADMIN")
            .authorities(List.of(
                    DatabaseAuthority.READ_DOCUMENTS,
                    DatabaseAuthority.ADD_DOCUMENTS,
                    DatabaseAuthority.UPDATE_DOCUMENTS,
                    DatabaseAuthority.REMOVE_DOCUMENTS,
                    DatabaseAuthority.GET_INDEXES,
                    DatabaseAuthority.CREATE_INDEX,
                    DatabaseAuthority.REMOVE_INDEX,
                    DatabaseAuthority.GET_COLLECTIONS,
                    DatabaseAuthority.CREATE_COLLECTION,
                    DatabaseAuthority.REMOVE_COLLECTION,
                    DatabaseAuthority.GET_DATABASES,
                    DatabaseAuthority.CREATE_DATABASE,
                    DatabaseAuthority.REMOVE_DATABASE)
            )
            .build();

    StoredDatabaseRole ROOT_ADMIN = StoredDatabaseRole.builder()
            .role("ROOT_ADMIN")
            .authorities(List.of(
                    DatabaseAuthority.READ_DOCUMENTS,
                    DatabaseAuthority.ADD_DOCUMENTS,
                    DatabaseAuthority.UPDATE_DOCUMENTS,
                    DatabaseAuthority.REMOVE_DOCUMENTS,
                    DatabaseAuthority.GET_INDEXES,
                    DatabaseAuthority.CREATE_INDEX,
                    DatabaseAuthority.REMOVE_INDEX,
                    DatabaseAuthority.GET_COLLECTIONS,
                    DatabaseAuthority.CREATE_COLLECTION,
                    DatabaseAuthority.REMOVE_COLLECTION,
                    DatabaseAuthority.GET_DATABASES,
                    DatabaseAuthority.CREATE_DATABASE,
                    DatabaseAuthority.REMOVE_DATABASE,
                    DatabaseAuthority.ADD_USER,
                    DatabaseAuthority.REMOVE_USER)
            )
            .build();

    List<StoredDatabaseRole> DEFAULT_ROLES = List.of(
            READER, USER, OWNER, MANAGER, ADMIN, ROOT_ADMIN
    );
}
