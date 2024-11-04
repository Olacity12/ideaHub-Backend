package com.ideahub.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "posts")
public class Post {

    @Id
    private String id;
    private String userId;          // ID of the post creator
    private String username;         // Username of the post creator
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int upvotes;
    private int downvotes;
    private Boolean isTechnicalOpen; // Allows null values for default setting
    private List<Contributor> topContributors; // List of top contributors with userId and username
    private List<String> commentIds; // List of comment IDs for quick access to comment count

    @Data
    public static class Contributor {
        private String userId;
        private String username;

        public Contributor(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }
    }
}
