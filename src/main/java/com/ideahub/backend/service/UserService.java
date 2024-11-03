package com.ideahub.backend.service;

import org.springframework.stereotype.Service;
import com.ideahub.backend.model.User;
import com.ideahub.backend.repository.UserRepository;
import com.ideahub.backend.utils.UsernameGenerator;
import com.ideahub.backend.utils.TokenGenerator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UsernameGenerator usernameGenerator;

    public UserService(UserRepository userRepository, UsernameGenerator usernameGenerator) {
        this.userRepository = userRepository;
        this.usernameGenerator = usernameGenerator;
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
        return userRepository.save(newUser);
    }

    public String generateUserToken(User user) {
        return TokenGenerator.generateToken(user.getId());
    }
}
