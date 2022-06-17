package com.atypon.nosql.api.controllers;

import com.atypon.nosql.api.services.DatabaseUsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
public class UsersRestController {
    private final DatabaseUsersService usersService;

    public UsersRestController(DatabaseUsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@RequestBody Map<String, Object> userData) {
        usersService.addUser(userData);
        return ResponseEntity.ok("Added [1] user");
    }

    @GetMapping("/users")
    public ResponseEntity<Collection<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(usersService.getUsers());
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        usersService.removeUser(username);
        return ResponseEntity.ok("Deleted [1] user");
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<String> updateUser(
            @RequestBody Map<String, Object> userData,
            @PathVariable("username") String username) {
        usersService.updateUser(username, userData);
        return ResponseEntity.ok("Updated [1] user");
    }
}
