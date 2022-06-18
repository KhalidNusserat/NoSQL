package com.atypon.nosql.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class DatabaseUser implements UserDetails {
    private final String username;

    private final String password;

    private final Collection<DatabaseRole> roles;

    private DatabaseUser(String username, String password, Collection<DatabaseRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public static DatabaseUserBuilder builder() {
        return new DatabaseUserBuilder();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static class DatabaseUserBuilder {
        private String username = "";

        private String password = "";

        private Collection<DatabaseRole> roles = List.of();

        public DatabaseUserBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public DatabaseUserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public DatabaseUserBuilder setRoles(Collection<DatabaseRole> roles) {
            this.roles = roles;
            return this;
        }

        public DatabaseUser build() {
            return new DatabaseUser(username, password, roles);
        }
    }
}
