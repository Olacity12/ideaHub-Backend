package com.ideahub.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ideahub.backend.model.User;
import com.ideahub.backend.model.UserProfile;
import com.ideahub.backend.repository.UserRepository;
import com.ideahub.backend.utils.UsernameGenerator;
import com.ideahub.backend.utils.TokenGenerator;
import com.ideahub.backend.repository.UserProfileRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UsernameGenerator usernameGenerator;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserService(UserRepository userRepository, UsernameGenerator usernameGenerator, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.usernameGenerator = usernameGenerator;
        this.userProfileRepository = userProfileRepository;
    }

    public User registerUser(String googleId, String email, String provider) {
        User existingUser = userRepository.findByGoogleId(googleId);
        if (existingUser != null) {
            return existingUser;
        }

        String username = usernameGenerator.generateUniqueUsername();

        User newUser = new User();
        newUser.setGoogleId(googleId);
        newUser.setEmail(email);
        newUser.setProvider(provider);
        newUser.setUsername(username);

        User savedUser = userRepository.save(newUser);

        if (!userProfileRepository.existsById(savedUser.getId())) {
            UserProfile newUserProfile = new UserProfile(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    Collections.emptyList(), // List<String> for posts
                    Collections.emptyList(), // List<String> for comments
                    Collections.emptyMap(),  // Map<String, String> for votes
                    0,                       // aggregateScore
                    0,                       // numberOfPosts
                    0,                       // numberOfComments
                    0                        // upvotesReceived
            );
            userProfileRepository.save(newUserProfile);
        }

        return savedUser;
    }

    public String generateUserToken(User user) {
        return TokenGenerator.generateToken(user.getId());
    }

    // New method to retrieve username by userId
    public String getUsernameById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getUsername).orElse("Unknown User");
    }
}