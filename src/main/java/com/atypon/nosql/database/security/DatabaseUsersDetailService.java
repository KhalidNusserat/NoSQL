package com.atypon.nosql.database.security;

import com.atypon.nosql.database.collection.BasicIndexedDocumentsCollection;
import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import com.atypon.nosql.database.index.IndexFactory;
import com.atypon.nosql.database.io.IOEngine;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseUsersDetailService implements UserDetailsService {

    private final DocumentFactory documentFactory;

    private final IndexedDocumentsCollection usersCollection;

    private final PasswordEncoder passwordEncoder;

    public DatabaseUsersDetailService(
            DocumentFactory documentFactory,
            IndexFactory indexFactory,
            IOEngine ioEngine,
            PasswordEncoder passwordEncoder) {
        this.documentFactory = documentFactory;
        this.passwordEncoder = passwordEncoder;
        Path usersDirectory = Path.of("./users/");
        usersCollection = BasicIndexedDocumentsCollection.builder()
                .setDocumentFactory(documentFactory)
                .setDocumentsPath(usersDirectory)
                .setIndexFactory(indexFactory)
                .setIOEngine(ioEngine)
                .build();
        Document usernameIndex = documentFactory.createFromString("{username: null}");
        if (!usersCollection.containsIndex(usernameIndex)) {
            usersCollection.createIndex(usernameIndex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String usernameCriteriaString = String.format("{username: %s}", username);
        Document usernameCriteria = documentFactory.createFromString(usernameCriteriaString);
        List<Document> matchedUsers = usersCollection.getAllThatMatch(usernameCriteria);
        if (matchedUsers.size() != 1) {
            throw new RuntimeException("More than one user with the same username");
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

    public void addUser(String username, String password, List<String> roles) {
        String encodedPassword = passwordEncoder.encode(password);
        String newUserDocumentString = String.format(
                "{username: \"%s\", password: \"%s\", roles: %s}",
                username,
                encodedPassword,
                getRolesArrayString(roles)
        );
        Document newUserDocument = documentFactory.createFromString(newUserDocumentString);
        newUserDocument = documentFactory.appendId(newUserDocument);
        usersCollection.addDocument(newUserDocument);
    }

    private String getRolesArrayString(List<String> roles) {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < roles.size(); i++) {
            stringBuilder.append('"').append(roles.get(i)).append('"');
            if (i < roles.size() - 1) {
                stringBuilder.append(',');
            }
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
