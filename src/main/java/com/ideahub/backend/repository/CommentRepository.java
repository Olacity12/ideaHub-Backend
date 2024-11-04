package com.ideahub.backend.repository;

import com.ideahub.backend.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByPostIdAndParentCommentIdIsNull(String postId); // Fetch top-level comments for a post

    List<Comment> findByParentCommentId(String parentCommentId); // Fetch replies to a specific comment
}
