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
import java.util.List;

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
        // Check if the user already exists based on Google ID
        User existingUser = userRepository.findByGoogleId(googleId);
        if (existingUser != null) {
            return existingUser;
        }

        // Generate a unique username
        String username = usernameGenerator.generateUniqueUsername();

        // Create and save a new user instance
        User newUser = new User();
        newUser.setGoogleId(googleId);
        newUser.setEmail(email);
        newUser.setProvider(provider);
        newUser.setUsername(username);

        // Save the user and return the saved instance, which will have the ID set
        User savedUser = userRepository.save(newUser);

        // Create a UserProfile if it does not already exist
        if (!userProfileRepository.existsById(savedUser.getId())) {
            UserProfile newUserProfile = new UserProfile(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    Collections.emptyList(), // Properly initializes posts as an empty list
                    Collections.emptyList(), // Properly initializes comments as an empty list
                    Collections.emptyList(), // Properly initializes upvotedPosts as an empty list
                    Collections.emptyList(), // Properly initializes downvotedPosts as an empty list
                    0,                        // Initial aggregate score
                    0,                        // Initial number of posts
                    0,                        // Initial number of comments
                    0                         // Initial upvotes received
            );
            userProfileRepository.save(newUserProfile);
        }

        return savedUser;
    }

    public String generateUserToken(User user) {
        return TokenGenerator.generateToken(user.getId());
    }
}
