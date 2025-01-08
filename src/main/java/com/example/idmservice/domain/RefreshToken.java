package com.example.idmservice.domain;

import com.example.idmservice.domain.type.TokenStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
public class RefreshToken {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer     id;
    private String      token;

    private Integer     userId;

    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;
    private Instant expireTime;
    private Instant     maxLifeTime;

    public Integer getId()
    {
        return id;
    }

    public RefreshToken setId(Integer id)
    {
        this.id = id;
        return this;
    }

    public String getToken()
    {
        return token;
    }

    public RefreshToken setToken(String token)
    {
        this.token = token;
        return this;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public RefreshToken setUserId(Integer userId)
    {
        this.userId = userId;
        return this;
    }

    public TokenStatus getTokenStatus()
    {
        return tokenStatus;
    }

    public RefreshToken setTokenStatus(TokenStatus tokenStatus)
    {
        this.tokenStatus = tokenStatus;
        return this;
    }

    public Instant getExpireTime()
    {
        return expireTime;
    }

    public RefreshToken setExpireTime(Instant expireTime)
    {
        this.expireTime = expireTime;
        return this;
    }

    public Instant getMaxLifeTime()
    {
        return maxLifeTime;
    }

    public RefreshToken setMaxLifeTime(Instant maxLifeTime)
    {
        this.maxLifeTime = maxLifeTime;
        return this;
    }
}
