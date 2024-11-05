package com.ideahub.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;
    private String postId;
    private String userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private int upvotes;
    private int downvotes;
    private Boolean isAnswered;
    private String parentCommentId;
    private List<Comment> replies;

    // New fields to track user votes
    private Set<String> upvotedUserIds = new HashSet<>();
    private Set<String> downvotedUserIds = new HashSet<>();

    // Constructor
    public Comment(String postId, String userId, String content, String parentCommentId) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.upvotes = 0;
        this.downvotes = 0;
        this.isAnswered = false;
        this.parentCommentId = parentCommentId;
    }
}