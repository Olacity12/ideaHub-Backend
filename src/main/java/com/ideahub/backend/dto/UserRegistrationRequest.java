package com.ideahub.backend.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String googleId;
    private String email;
    private String provider;
}
