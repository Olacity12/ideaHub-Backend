package com.ideahub.backend.utils;

import java.util.Base64;

public class TokenGenerator {

    public static String generateToken(String userId) {
        // Simple encoding for demonstration purposes; not secure for production
        String tokenData = "userId:" + userId;
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }

    public static String extractUserIdFromToken(String token) {
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String decodedString = new String(decodedBytes);
        if (decodedString.startsWith("userId:")) {
            return decodedString.split(":")[1];
        }
        return null;
    }
}
