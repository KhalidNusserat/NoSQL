package com.atypon.nosql.users;

import lombok.Builder;

import java.util.List;

public record DatabaseRole(String role, List<DatabaseAuthority> authorities) {

    @Builder
    public DatabaseRole {}

    public record StoredDatabaseRole(String role, List<String> authorities) {

        @Builder
        public StoredDatabaseRole {}
    }
}
