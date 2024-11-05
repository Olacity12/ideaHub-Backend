package com.ideahub.backend.service;

import com.ideahub.backend.model.Post;
import com.ideahub.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ideahub.backend.model.UserProfile;
import com.ideahub.backend.repository.UserProfileRepository;

import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final PostRepository postRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, PostRepository postRepository) {
        this.userProfileRepository = userProfileRepository;
        this.postRepository = postRepository;
    }

    public UserProfile getUserProfile(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public UserProfile createOrUpdateUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    // New method to add a post ID to the user's profile
    public void addPostToUserProfile(String userId, String postId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        if (userProfileOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            userProfile.getPosts().add(postId);
            userProfile.setNumberOfPosts(userProfile.getPosts().size());
            userProfileRepository.save(userProfile);
        }
    }

    // New method to add a comment ID to the user's profile
    public void addCommentToUserProfile(String userId, String commentId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        if (userProfileOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            userProfile.getComments().add(commentId);
            userProfile.setNumberOfComments(userProfile.getComments().size());
            userProfileRepository.save(userProfile);
        }
    }

    // Method to upvote a post
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

    // Method to downvote a post
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