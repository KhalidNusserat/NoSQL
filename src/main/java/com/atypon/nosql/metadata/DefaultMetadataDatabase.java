package com.atypon.nosql.metadata;

import com.atypon.nosql.DatabasesManager;
import com.atypon.nosql.collection.IndexedDocumentsCollection;
import com.atypon.nosql.document.Document;
import com.atypon.nosql.request.DatabaseOperation;
import com.atypon.nosql.request.DatabaseRequest;
import com.atypon.nosql.request.Payload;
import com.atypon.nosql.request.filters.DatabaseRequestsFiltersManager;
import com.atypon.nosql.request.handlers.DatabaseRequestHandler;
import com.atypon.nosql.security.DatabaseAuthority;
import com.atypon.nosql.security.DatabaseRole;
import com.atypon.nosql.security.DatabaseUser;
import com.atypon.nosql.security.DefaultRoles;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.atypon.nosql.metadata.MetadataDatabaseConstants.*;

@ToString
@Component
public class DefaultMetadataDatabase implements MetadataDatabase {

    private final DatabasesManager databasesManager;

    private final DatabaseRequestHandler operationsHandler;

    private final DatabaseRequestsFiltersManager filtersManager;

    public DefaultMetadataDatabase(
            DatabasesManager databasesManager,
            @Qualifier("operationsHandler") DatabaseRequestHandler operationsHandler,
            DatabaseRequestsFiltersManager filtersManager) {
        this.databasesManager = databasesManager;
        this.operationsHandler = operationsHandler;
        this.filtersManager = filtersManager;
        databasesManager.createDatabase(METADATA_DATABASE);
        createUsersCollection();
        createRolesCollection();
        createDefaultRoles();
        createDefaultAdmin();
    }

    private void createUsersCollection() {
        if (!databasesManager.getDatabase(METADATA_DATABASE).containsCollection(USERS_COLLECTION)) {
            databasesManager.getDatabase(METADATA_DATABASE)
                    .createCollection(
                            USERS_COLLECTION,
                            USERS_SCHEMA
                    );
            databasesManager.getDatabase(METADATA_DATABASE)
                    .getCollection(USERS_COLLECTION)
                    .createIndex(USERNAME_INDEX, true);
        }
    }

    private void createRolesCollection() {
        if (!databasesManager.getDatabase(METADATA_DATABASE).containsCollection(ROLES_COLLECTION)) {
            databasesManager.getDatabase(METADATA_DATABASE)
                    .createCollection(
                            ROLES_COLLECTION,
                            ROLES_SCHEMA
                    );
            databasesManager.getDatabase(METADATA_DATABASE)
                    .getCollection(ROLES_COLLECTION)
                    .createIndex(ROLE_INDEX, true);
        }
    }

    private void createDefaultRoles() {
        IndexedDocumentsCollection rolesCollection = databasesManager.getDatabase(METADATA_DATABASE)
                .getCollection(ROLES_COLLECTION);
        for (var role : DefaultRoles.DEFAULT_ROLES) {
            Document roleCriteria = Document.fromMap(Map.of("role", role.role()));
            if (!rolesCollection.contains(roleCriteria)) {
                Payload payload = Payload.builder()
                        .documents(List.of(Document.fromObject(role)))
                        .build();
                DatabaseRequest createRoleRequest = DatabaseRequest.builder()
                        .database(METADATA_DATABASE)
                        .collection(ROLES_COLLECTION)
                        .operation(DatabaseOperation.ADD_DOCUMENT)
                        .payload(payload)
                        .build();
                createRoleRequest = filtersManager.applyOn(createRoleRequest);
                operationsHandler.handle(createRoleRequest);
            }
        }
    }

    private void createDefaultAdmin() {
        IndexedDocumentsCollection usersCollection = databasesManager.getDatabase(METADATA_DATABASE)
                .getCollection(USERS_COLLECTION);
        Document rootAdminCriteria = Document.fromJson("{username: \"admin\"}");
        if (!usersCollection.contains(rootAdminCriteria)) {
            Payload payload = Payload.builder()
                    .documents(List.of(defaultAdmin))
                    .build();
            DatabaseRequest addRootAdminRequest = DatabaseRequest.builder()
                    .database(METADATA_DATABASE)
                    .collection(USERS_COLLECTION)
                    .operation(DatabaseOperation.ADD_DOCUMENT)
                    .payload(payload)
                    .build();
            addRootAdminRequest = filtersManager.applyOn(addRootAdminRequest);
            operationsHandler.handle(addRootAdminRequest);
        }
    }

    @Override
    public DatabaseUser findUser(String username) {
        Document userCriteria = Document.fromMap(Map.of("username", username));
        Optional<Document> user = databasesManager.getDatabase(METADATA_DATABASE)
                .getCollection(USERS_COLLECTION)
                .findFirst(userCriteria);
        if (user.isPresent()) {
            var storedUser = user.get().toObject(DatabaseUser.StoredDatabaseUser.class);
            List<DatabaseRole> roles = extractRoles(storedUser.roles());
            List<DatabaseAuthority> authorities = extractAuthorities(storedUser.authorities());
            return DatabaseUser.builder()
                    .username(username)
                    .password(storedUser.password())
                    .roles(roles)
                    .authorities(authorities)
                    .build();
        } else {
            throw new UsernameNotFoundException(username + " not found");
        }
    }

    private List<DatabaseRole> extractRoles(List<String> roles) {
        return roles.stream()
                .map(this::findRole)
                .toList();
    }

    private DatabaseRole findRole(String role) {
        Document roleCriteria = Document.fromMap(Map.of("role", role));
        Optional<Document> roleDocument = databasesManager.getDatabase(METADATA_DATABASE)
                .getCollection(ROLES_COLLECTION)
                .findFirst(roleCriteria);
        if (roleDocument.isPresent()) {
            var storedRole = roleDocument.get().toObject(DatabaseRole.StoredDatabaseRole.class);
            List<DatabaseAuthority> authorities = extractAuthorities(storedRole.authorities());
            return DatabaseRole.builder()
                    .role(role)
                    .authorities(authorities)
                    .build();
        } else {
            throw new RoleNotFoundException();
        }
    }

    private List<DatabaseAuthority> extractAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(DatabaseAuthority::from)
                .toList();
    }
}
