package com.atypon.nosql.users;

import com.atypon.nosql.metadata.MetadataDatabase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
