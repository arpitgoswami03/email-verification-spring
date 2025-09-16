package com.arpit.emailverification.controller;

import com.arpit.emailverification.model.LoginDetails;
import com.arpit.emailverification.model.User;
import com.arpit.emailverification.repo.UserRepo;
import com.arpit.emailverification.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public ResponseEntity<List<User>> getUser() {
        List<User> users = userService.getUsers();
        if(users.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDetails request) {
        if(request.getPassword().isEmpty() || request.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Boolean authenticated = userService.verify(request.getUsername(), request.getPassword());
        if (authenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User userDetails) {
        User user = userService.register(userDetails);
        if(user==null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyUser(@PathVariable String token) {
        boolean verified = userService.verifyToken(token);
        if (verified) {
            return ResponseEntity.ok("Email verified successfully! You can now log in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
    }
}
