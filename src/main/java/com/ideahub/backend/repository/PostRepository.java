package com.ideahub.backend.repository;

import com.ideahub.backend.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    // Additional custom queries can be added here if needed
}
