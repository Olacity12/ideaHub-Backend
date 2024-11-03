package com.ideahub.backend.repository;

import com.ideahub.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    User findByGoogleId(String googleId); // New method to find a user by Google ID
}
