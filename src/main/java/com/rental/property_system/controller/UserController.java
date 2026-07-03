package com.rental.property_system.controller;

import com.rental.property_system.entity.User;
import com.rental.property_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST request to create a new user. 
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // GET request to fetch all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}