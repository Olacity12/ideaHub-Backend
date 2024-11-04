package com.ideahub.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ideahub.backend.model.UserProfile;
import com.ideahub.backend.repository.UserProfileRepository;

import java.util.Optional;

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

    // New method to add a post ID to the user's profile
    public void addPostToUserProfile(String userId, String postId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        if (userProfileOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            userProfile.getPosts().add(postId);
            userProfile.setNumberOfPosts(userProfile.getPosts().size());
            userProfileRepository.save(userProfile);
        }
    }

    // New method to add a comment ID to the user's profile
    public void addCommentToUserProfile(String userId, String commentId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        if (userProfileOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            userProfile.getComments().add(commentId);
            userProfile.setNumberOfComments(userProfile.getComments().size());
            userProfileRepository.save(userProfile);
        }
    }
}
