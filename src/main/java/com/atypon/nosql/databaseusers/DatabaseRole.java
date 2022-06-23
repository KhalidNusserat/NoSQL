package com.atypon.nosql.databaseusers;

import org.springframework.security.core.GrantedAuthority;

public class DatabaseRole implements GrantedAuthority {
    private final String role;

    public DatabaseRole(String role) {
        this.role = role;
    }

    public static DatabaseRole of(String role) {
        return new DatabaseRole(role);
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + role;
    }
}
