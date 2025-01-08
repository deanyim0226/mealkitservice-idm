package com.example.idmservice.model.request;

public class RefreshRequest {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public RefreshRequest setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
