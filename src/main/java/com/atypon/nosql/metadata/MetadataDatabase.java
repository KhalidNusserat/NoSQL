package com.atypon.nosql.metadata;

import com.atypon.nosql.users.DatabaseUser;

public interface MetadataDatabase {

    DatabaseUser findUser(String username);
}
