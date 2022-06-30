package com.atypon.nosql.security;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record DatabaseUser(
        String username,
        String password,
        Collection<DatabaseRole> roles,
        Collection<DatabaseAuthority> authorities) implements UserDetails {

    @Builder
    public DatabaseUser {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<DatabaseAuthority> grantedAuthorities = new HashSet<>(authorities);
        for (DatabaseRole role : roles) {
            grantedAuthorities.addAll(role.authorities());
        }
        return grantedAuthorities;
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

    public record StoredDatabaseUser(
            String username,
            String password,
            List<String> roles,
            List<String> authorities) {

        @Builder
        public StoredDatabaseUser {
        }
    }
}
