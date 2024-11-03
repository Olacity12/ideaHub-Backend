package com.ideahub.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Document(collection = "userProfiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private String userId;
    private String username;
    private List<String> posts;
    private List<String> comments;
    private List<String> upvotedPosts;
    private List<String> downvotedPosts;
    private int aggregateScore;
    private int numberOfPosts;
    private int numberOfComments;
    private int upvotesReceived;
}
