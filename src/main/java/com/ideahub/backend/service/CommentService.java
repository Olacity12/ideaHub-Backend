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
    private final UserService userService; // Assuming UserService provides user info
    private final PostService postService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, PostService postService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
    }

    public Comment addComment(Comment comment) {
        comment.setUsername(userService.getUsernameById(comment.getUserId())); // Set username
        Comment savedComment = commentRepository.save(comment);

        if (comment.getParentCommentId() == null) { // Only add to post for top-level comments
            postService.addCommentIdToPost(comment.getPostId(), savedComment.getId());
        }

        return savedComment;
    }

    public List<Comment> getCommentsForPost(String postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIdIsNull(postId);
        for (Comment comment : comments) {
            List<Comment> replies = getRepliesForComment(comment.getId());
            comment.setReplies(replies);
        }
        return comments;
    }

    public List<Comment> getRepliesForComment(String commentId) {
        return commentRepository.findByParentCommentId(commentId);
    }

    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }
}
