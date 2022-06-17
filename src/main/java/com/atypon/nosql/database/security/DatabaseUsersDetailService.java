package com.atypon.nosql.database.security;

import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseUsersDetailService implements UserDetailsService {

    private final IndexedDocumentsCollection usersCollection;

    private final DocumentFactory documentFactory;

    public DatabaseUsersDetailService(
            IndexedDocumentsCollection usersCollection, DocumentFactory documentFactory) {
        this.usersCollection = usersCollection;
        this.documentFactory = documentFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String usernameCriteriaString = String.format("{username: %s}", username);
        Document usernameCriteria = documentFactory.createFromString(usernameCriteriaString);
        List<Document> matchedUsers = usersCollection.getAllThatMatch(usernameCriteria);
        if (matchedUsers.size() > 1) {
            throw new RuntimeException("More than one user with the same username");
        } else if (matchedUsers.size() == 0) {
            throw new RuntimeException("User does not exist");
        }
        Map<String, Object> userDocument = matchedUsers.get(0).getAsMap();
        String password = (String) userDocument.get("password");
        List<String> roles = (List<String>) userDocument.get("roles");
        return DatabaseUser.builder()
                .setUsername(username)
                .setPassword(password)
                .setRoles(roles.stream().map(DatabaseRole::of).toList())
                .build();
    }
}
