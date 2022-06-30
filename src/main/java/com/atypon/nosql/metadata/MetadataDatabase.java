package com.atypon.nosql.metadata;

import com.atypon.nosql.security.DatabaseUser;

public interface MetadataDatabase {

    DatabaseUser findUser(String username);
}
