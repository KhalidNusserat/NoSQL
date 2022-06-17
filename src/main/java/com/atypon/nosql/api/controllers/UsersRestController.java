package com.atypon.nosql.api.controllers;

import com.atypon.nosql.database.security.DatabaseUsersDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UsersRestController {
    private final DatabaseUsersDetailService usersService;

    public UsersRestController(DatabaseUsersDetailService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@RequestBody Map<String, Object> userData) {
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        List<String> roles = (List<String>) userData.get("roles");
        usersService.addUser(username, password, roles);
        return ResponseEntity.ok("User added");
    }
}
