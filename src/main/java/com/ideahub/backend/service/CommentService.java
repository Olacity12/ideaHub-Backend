package com.ideahub.backend.service;

import com.ideahub.backend.model.Comment;
import com.ideahub.backend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;
    private final UserProfileService userProfileService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, PostService postService, UserProfileService userProfileService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
        this.userProfileService = userProfileService;
    }

    public Comment addComment(Comment comment) {
        // Set the username based on the userId
        comment.setUsername(userService.getUsernameById(comment.getUserId()));

        // Save the comment to the database
        Comment savedComment = commentRepository.save(comment);

        // If it's a top-level comment, add its ID to the post's comment list
        if (comment.getParentCommentId() == null) {
            postService.addCommentIdToPost(comment.getPostId(), savedComment.getId());
        }

        // Update the user's profile to include the new comment ID
        userProfileService.addCommentToUserProfile(comment.getUserId(), savedComment.getId());

        return savedComment;
    }

    public List<Comment> getCommentsForPost(String postId) {
        // Retrieve top-level comments for the post
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIdIsNull(postId);

        // Populate replies for each top-level comment
        for (Comment comment : comments) {
            List<Comment> replies = getRepliesForComment(comment.getId());
            comment.setReplies(replies);
        }

        return comments;
    }

    public List<Comment> getRepliesForComment(String commentId) {
        // Retrieve replies for a specific comment
        return commentRepository.findByParentCommentId(commentId);
    }

    public void deleteComment(String commentId) {
        // Delete the comment by ID
        commentRepository.deleteById(commentId);
    }
}
