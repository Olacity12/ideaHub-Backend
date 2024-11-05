package com.ideahub.backend.service;

import com.ideahub.backend.model.Comment;
import com.ideahub.backend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

        // If it's a top-level comment, associate it with the post
        if (comment.getParentCommentId() == null) {
            postService.addCommentIdToPost(comment.getPostId(), savedComment.getId());
        }

        // Update the user's profile to include the new comment
        userProfileService.addCommentToUserProfile(comment.getUserId(), savedComment.getId());

        return savedComment;
    }

    public List<Comment> getCommentsForPost(String postId) {
        // Fetch all comments for the given post in a single query
        List<Comment> allComments = commentRepository.findByPostId(postId);

        // Map for easy lookup of comments by ID
        Map<String, Comment> commentMap = new HashMap<>();
        List<Comment> topLevelComments = new ArrayList<>();

        // Populate the map and identify top-level comments
        allComments.forEach(comment -> {
            commentMap.put(comment.getId(), comment);
            if (comment.getParentCommentId() == null) {
                topLevelComments.add(comment);
            }
        });

        // Organize replies under their respective parent comments
        allComments.forEach(comment -> {
            if (comment.getParentCommentId() != null) {
                Comment parentComment = commentMap.get(comment.getParentCommentId());
                if (parentComment != null) {
                    parentComment.getReplies().add(comment);
                }
            }
        });

        return topLevelComments;
    }

    public List<Comment> getRepliesForComment(String commentId) {
        return commentRepository.findByParentCommentId(commentId);
    }

    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public void upvoteComment(String userId, String commentId) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            if (!comment.getUpvotedUserIds().contains(userId)) {
                // Remove downvote if previously downvoted
                if (comment.getDownvotedUserIds().remove(userId)) {
                    comment.setDownvotes(comment.getDownvotes() - 1);
                }
                // Add upvote
                comment.getUpvotedUserIds().add(userId);
                comment.setUpvotes(comment.getUpvotes() + 1);
                commentRepository.save(comment);
            }
        });
    }

    public void downvoteComment(String userId, String commentId) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            if (!comment.getDownvotedUserIds().contains(userId)) {
                // Remove upvote if previously upvoted
                if (comment.getUpvotedUserIds().remove(userId)) {
                    comment.setUpvotes(comment.getUpvotes() - 1);
                }
                // Add downvote
                comment.getDownvotedUserIds().add(userId);
                comment.setDownvotes(comment.getDownvotes() + 1);
                commentRepository.save(comment);
            }
        });
    }
}