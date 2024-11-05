package com.ideahub.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private List<String> posts = new ArrayList<>();
    private List<String> comments = new ArrayList<>();
    private Map<String, String> votes = new HashMap<>();
    private int aggregateScore;
    private int numberOfPosts;
    private int numberOfComments;
    private int upvotesReceived;
}