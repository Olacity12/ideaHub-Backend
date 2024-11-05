package com.ideahub.backend.service;

import com.ideahub.backend.model.Comment;
import com.ideahub.backend.model.Post;
import com.ideahub.backend.model.UserProfile;
import com.ideahub.backend.repository.CommentRepository;
import com.ideahub.backend.repository.PostRepository;
import com.ideahub.backend.repository.UserProfileRepository;
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
    private final UserProfileRepository userProfileRepository ;

    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserProfileService userProfileService, UserProfileRepository userProfileRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userProfileService = userProfileService;
        this.userProfileRepository = userProfileRepository;
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

    // Method to get all posts with the user's vote status
    public List<PostWithVoteStatus> getAllPostsWithUserVoteStatus(String userId) {
        // Retrieve all posts
        List<Post> posts = postRepository.findAll();

        // Retrieve the user's profile
        UserProfile userProfile = userProfileRepository.findById(userId).orElse(null);

        // Map posts to include the user's vote status
        return posts.stream().map(post -> {
            String voteStatus = null;
            if (userProfile != null) {
                voteStatus = userProfile.getVotes().get(post.getId()); // "upvote", "downvote", or null
            }
            return new PostWithVoteStatus(post, voteStatus);
        }).collect(Collectors.toList());
    }

    // Inner class to include vote status in the response
    public static class PostWithVoteStatus {
        private final Post post;
        private final String voteStatus;

        public PostWithVoteStatus(Post post, String voteStatus) {
            this.post = post;
            this.voteStatus = voteStatus;
        }

        public Post getPost() {
            return post;
        }

        public String getVoteStatus() {
            return voteStatus;
        }
    }

    // Method to handle upvoting a post
    public void upvotePost(String userId, String postId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        Optional<Post> postOpt = postRepository.findById(postId);

        if (userProfileOpt.isPresent() && postOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            Post post = postOpt.get();

            // Check the user's existing vote for this post
            String existingVote = userProfile.getVotes().get(postId);

            if (!"upvote".equals(existingVote)) {
                // If the user had downvoted before, remove the downvote
                if ("downvote".equals(existingVote)) {
                    post.setDownvotes(post.getDownvotes() - 1);
                }

                // Add the upvote
                post.setUpvotes(post.getUpvotes() + 1);
                userProfile.getVotes().put(postId, "upvote");

                // Save the changes
                userProfileRepository.save(userProfile);
                postRepository.save(post);
            }
        }
    }

    // Method to handle downvoting a post
    public void downvotePost(String userId, String postId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        Optional<Post> postOpt = postRepository.findById(postId);

        if (userProfileOpt.isPresent() && postOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            Post post = postOpt.get();

            // Check the user's existing vote for this post
            String existingVote = userProfile.getVotes().get(postId);

            if (!"downvote".equals(existingVote)) {
                // If the user had upvoted before, remove the upvote
                if ("upvote".equals(existingVote)) {
                    post.setUpvotes(post.getUpvotes() - 1);
                }

                // Add the downvote
                post.setDownvotes(post.getDownvotes() + 1);
                userProfile.getVotes().put(postId, "downvote");

                // Save the changes
                userProfileRepository.save(userProfile);
                postRepository.save(post);
            }
        }
    }
}