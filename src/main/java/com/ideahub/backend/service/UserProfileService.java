package com.ideahub.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ideahub.backend.model.UserProfile;
import com.ideahub.backend.repository.UserProfileRepository;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile getUserProfile(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public UserProfile createOrUpdateUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
}
