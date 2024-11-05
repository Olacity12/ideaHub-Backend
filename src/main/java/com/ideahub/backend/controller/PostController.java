package com.ideahub.backend.controller;

import com.ideahub.backend.model.Post;
import com.ideahub.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Endpoint to create a new post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    // Endpoint to get a post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable String postId) {
        Optional<Post> post = postService.getPostById(postId);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint to update a post (excluding userId and username)
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable String postId, @RequestBody Post updatedPost) {
        Optional<Post> updated = postService.updatePost(postId, updatedPost);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint to delete a post
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Endpoint to add a comment ID to a post
    @PostMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> addCommentToPost(@PathVariable String postId, @PathVariable String commentId) {
        postService.addCommentIdToPost(postId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Endpoint to upvote a post
    @PostMapping("/{postId}/upvote")
    public ResponseEntity<Void> upvotePost(@PathVariable String postId, @RequestParam String userId) {
        postService.upvotePost(userId, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Endpoint to downvote a post
    @PostMapping("/{postId}/downvote")
    public ResponseEntity<Void> downvotePost(@PathVariable String postId, @RequestParam String userId) {
        postService.downvotePost(userId, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Endpoint to get all posts with the user's vote status
    @GetMapping
    public ResponseEntity<List<PostService.PostWithVoteStatus>> getAllPosts(@RequestParam String userId) {
        List<PostService.PostWithVoteStatus> posts = postService.getAllPostsWithUserVoteStatus(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

}