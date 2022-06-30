package com.atypon.nosql.security;

import com.atypon.nosql.metadata.MetadataDatabase;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@ToString
@Service
public class DatabaseUsersDetailService implements UserDetailsService {

    private final MetadataDatabase metadataDatabase;

    public DatabaseUsersDetailService(MetadataDatabase metadataDatabase) {
        this.metadataDatabase = metadataDatabase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return metadataDatabase.findUser(username);
    }
}
