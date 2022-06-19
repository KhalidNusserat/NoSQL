package com.atypon.nosql.api.services;

import com.atypon.nosql.database.collection.IndexedDocumentsCollection;
import com.atypon.nosql.database.document.Document;
import com.atypon.nosql.database.document.DocumentFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class DefaultDatabaseUsersService implements DatabaseUsersService {
    private final IndexedDocumentsCollection usersCollection;

    private final DocumentFactory documentFactory;

    private final PasswordEncoder passwordEncoder;

    public DefaultDatabaseUsersService(
            IndexedDocumentsCollection usersCollection,
            DocumentFactory documentFactory,
            PasswordEncoder passwordEncoder) {
        this.usersCollection = usersCollection;
        this.documentFactory = documentFactory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void addUser(Map<String, Object> userData) {
        String password = (String) userData.get("password");
        userData.put("password", passwordEncoder.encode(password));
        Document user = documentFactory.createFromMap(userData);
        usersCollection.addDocument(user);
    }

    @Override
    public void updateUser(String username, Map<String, Object> updatedUserData) {
        Document oldUserCriteria = documentFactory.createFromMap(Map.of("username", username));
        String password = (String) updatedUserData.get("password");
        updatedUserData.put("password", passwordEncoder.encode(password));
        Document updatedUser = documentFactory.createFromMap(updatedUserData);
        usersCollection.updateDocument(oldUserCriteria, updatedUser);
    }

    @Override
    public void removeUser(String username) {
        if (username.equals("admin")) {
            throw new RuntimeException("Admin cannot be deleted");
        }
        Document user = documentFactory.createFromMap(Map.of("username", username));
        usersCollection.removeAllThatMatch(user);
    }

    @Override
    public Collection<Map<String, Object>> getUsers() {
       Collection<Document> users = usersCollection.getAll();
       Collection<Document> usersWithoutPasswords = removePasswords(users);
       return documentsToMaps(usersWithoutPasswords);
    }

    private Collection<Map<String, Object>> documentsToMaps(Collection<Document> documents) {
        return documents.stream().map(Document::getAsMap).toList();
    }

    private Collection<Document> removePasswords(Collection<Document> users) {
        Collection<Map<String, Object>> maps = documentsToMaps(users);
        maps.forEach(map -> map.remove("password"));
        return maps.stream().map(documentFactory::createFromMap).toList();
    }
}
