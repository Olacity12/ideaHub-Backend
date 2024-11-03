package com.ideahub.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ideahub.backend.model.User;
import com.ideahub.backend.service.UserService;
import com.ideahub.backend.dto.UserRegistrationRequest;
import com.ideahub.backend.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request.getGoogleId(), request.getEmail(), request.getProvider());
        String token = userService.generateUserToken(user);

        UserResponse response = new UserResponse(user.getId(), user.getUsername(), token);
        return ResponseEntity.ok(response);
    }
}
