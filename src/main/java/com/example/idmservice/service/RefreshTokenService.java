package com.example.idmservice.service;

import com.example.idmservice.domain.RefreshToken;
import com.example.idmservice.domain.User;

public interface RefreshTokenService {


    public RefreshToken saveRefreshToken(RefreshToken refreshToken);

    public void updateRefreshTokenExpireTime(RefreshToken token);

    public RefreshToken verifyRefreshToken(String token);

    public void expireRefreshToken(RefreshToken token);

    public void revokeRefreshToken(RefreshToken token);

    public User getUserFromRefreshToken(RefreshToken refreshToken);

}
