package com.example.idmservice.model.request;

public class AuthenticateRequest {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public AuthenticateRequest setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
