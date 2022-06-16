package com.atypon.nosql;

import com.atypon.nosql.database.security.DatabaseUsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UsersRestController {
    private final DatabaseUsersService usersService;

    public UsersRestController(DatabaseUsersService usersService) {
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
