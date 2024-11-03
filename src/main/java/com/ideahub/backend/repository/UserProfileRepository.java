package com.ideahub.backend.repository;

import com.ideahub.backend.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    UserProfile findByUserId(String userId);
}
