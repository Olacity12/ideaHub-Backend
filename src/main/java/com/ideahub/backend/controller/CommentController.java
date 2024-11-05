package com.ideahub.backend.controller;

import com.ideahub.backend.model.Comment;
import com.ideahub.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable String postId, @RequestBody Comment comment) {
        comment.setPostId(postId);
        Comment createdComment = commentService.addComment(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    // Add a reply to an existing comment
    @PostMapping("/{commentId}/replies")
    public ResponseEntity<Comment> addReply(@PathVariable String postId, @PathVariable String commentId, @RequestBody Comment reply) {
        reply.setPostId(postId);
        reply.setParentCommentId(commentId); // Set the parentCommentId to link it to the parent
        Comment createdReply = commentService.addComment(reply);
        return new ResponseEntity<>(createdReply, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsForPost(@PathVariable String postId) {
        List<Comment> comments = commentService.getCommentsForPost(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // Endpoint to upvote a comment
    @PostMapping("/{commentId}/upvote")
    public ResponseEntity<Void> upvoteComment(@PathVariable String commentId, @RequestParam String userId) {
        commentService.upvoteComment(userId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Endpoint to downvote a comment
    @PostMapping("/{commentId}/downvote")
    public ResponseEntity<Void> downvoteComment(@PathVariable String commentId, @RequestParam String userId) {
        commentService.downvoteComment(userId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}