package com.atypon.nosql.databaseusers;

import java.util.Collection;
import java.util.Map;

public interface DatabaseUsersService {
    void addUser(Map<String, Object> userData);

    void updateUser(String username, Map<String, Object> updatedUserData);

    void removeUser(String username);

    Collection<Map<String, Object>> getUsers();
}
