package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabaseUsersService;
import com.atypon.nosql.synchronisation.SynchronisationService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class UsersRestController {
    private final DatabaseUsersService usersService;

    private final SynchronisationService synchronisationService;

    public UsersRestController(
            DatabaseUsersService usersService,
            SynchronisationService synchronisationService) {
        this.usersService = usersService;
        this.synchronisationService = synchronisationService;
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(usersService.getUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@RequestBody Map<String, Object> userData) {
        usersService.addUser(userData);
        synchronisationService
                .method(HttpMethod.POST)
                .requestBody(userData)
                .url("/users")
                .synchronise();
        return ResponseEntity.ok("Added [1] user");
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        usersService.removeUser(username);
        synchronisationService
                .method(HttpMethod.DELETE)
                .url("/users/{username}")
                .parameters(username)
                .synchronise();
        return ResponseEntity.ok("Deleted [1] user");
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<String> updateUser(
            @PathVariable("username") String username,
            @RequestBody Map<String, Object> userData) {
        usersService.updateUser(username, userData);
        synchronisationService
                .method(HttpMethod.PUT)
                .requestBody(userData)
                .url("/users/{username}")
                .parameters(username)
                .synchronise();
        return ResponseEntity.ok("Updated [1] user");
    }
}
