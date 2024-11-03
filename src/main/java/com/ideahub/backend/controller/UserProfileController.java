package com.ideahub.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ideahub.backend.model.UserProfile;
import com.ideahub.backend.service.UserProfileService;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userId) {
        UserProfile userProfile = userProfileService.getUserProfile(userId);
        if (userProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping
    public ResponseEntity<UserProfile> createOrUpdateUserProfile(@RequestBody UserProfile userProfile) {
        UserProfile savedProfile = userProfileService.createOrUpdateUserProfile(userProfile);
        return ResponseEntity.ok(savedProfile);
    }
}
