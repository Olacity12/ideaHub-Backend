package com.ideahub.backend.service;

import com.ideahub.backend.model.Comment;
import com.ideahub.backend.model.Post;
import com.ideahub.backend.repository.CommentRepository;
import com.ideahub.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserProfileService userProfileService;

    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserProfileService userProfileService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userProfileService = userProfileService;
    }

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setUpvotes(0);
        post.setDownvotes(0);
        post.setTopContributors(List.of());
        post.setCommentIds(List.of());

        if (post.getIsTechnicalOpen() == null) {
            post.setIsTechnicalOpen(true);
        }

        Post savedPost = postRepository.save(post);

        // Update the user's profile with the new post ID
        userProfileService.addPostToUserProfile(post.getUserId(), savedPost.getId());

        return savedPost;
    }

    public Optional<Post> getPostById(String postId) {
        // Retrieve the post from the repository
        Optional<Post> postOpt = postRepository.findById(postId);

        if (postOpt.isPresent()) {
            Post post = postOpt.get();

            // Fetch the comments for the post
            List<Comment> comments = post.getCommentIds().stream()
                    .map(commentRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            // Populate replies for each comment
            for (Comment comment : comments) {
                List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());
                comment.setReplies(replies);
            }

            // Set the populated comments to the post
            post.setComments(comments);
            return Optional.of(post);
        }

        return Optional.empty();
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