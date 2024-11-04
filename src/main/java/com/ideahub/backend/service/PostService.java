package com.ideahub.backend.service;

import com.ideahub.backend.model.Post;
import com.ideahub.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setUpvotes(0);
        post.setDownvotes(0);
        post.setTopContributors(List.of());
        post.setCommentIds(List.of()); // Initialize as empty list for new posts

        if (post.getIsTechnicalOpen() == null) {
            post.setIsTechnicalOpen(true); // Default to true if not specified
        }

        return postRepository.save(post);
    }

    public Optional<Post> getPostById(String postId) {
        return postRepository.findById(postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> updatePost(String postId, Post updatedPost) {
        return postRepository.findById(postId).map(existingPost -> {
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());
            existingPost.setIsTechnicalOpen(updatedPost.getIsTechnicalOpen());
            existingPost.setTopContributors(updatedPost.getTopContributors());
            existingPost.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(existingPost);
        });
    }

    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }

    // Add a comment ID to the post
    public void addCommentIdToPost(String postId, String commentId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.getCommentIds().add(commentId);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
        });
    }

    // Remove a comment ID from the post
    public void removeCommentIdFromPost(String postId, String commentId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.getCommentIds().remove(commentId);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
        });
    }
}
