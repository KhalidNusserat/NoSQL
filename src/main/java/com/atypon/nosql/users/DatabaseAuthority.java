package com.atypon.nosql.users;

import org.springframework.security.core.GrantedAuthority;

public class DatabaseAuthority implements GrantedAuthority {

    private final String authority;

    public DatabaseAuthority(String authority) {
        this.authority = authority;
    }

    public static DatabaseAuthority from(String authority) {
        return new DatabaseAuthority(authority);
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
