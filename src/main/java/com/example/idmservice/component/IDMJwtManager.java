package com.example.idmservice.component;

import com.example.idmservice.domain.JWTManager;
import com.example.idmservice.domain.RefreshToken;
import com.example.idmservice.domain.User;
import com.example.idmservice.domain.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Component
public class IDMJwtManager {

    private final JWTManager jwtManager;

    //@Value("${idm.key-file-name}")
    private final String keyFileName = "ec-key.json";
    //@Value("${idm.access-token-expire}")
    private final Duration accessTokenExpire = Duration.ofMinutes(30);
    //@Value("${idm.refresh-token-expire}")
    private final Duration refreshTokenExpire = Duration.ofHours(12);

    //@Value("${idm.max-refresh-token-life-time}")
    private final Duration maxRefreshTokenLifeTime = Duration.ofDays(30);


    @Autowired
    public IDMJwtManager() throws IOException, ParseException, JOSEException {
        this.jwtManager =
                new JWTManager.Builder()
                        .keyFileName(keyFileName)
                        .accessTokenExpire(accessTokenExpire)
                        .maxRefreshTokenLifeTime(maxRefreshTokenLifeTime)
                        .refreshTokenExpire(refreshTokenExpire)
                        .build();
    }

    private void verifyJWT(SignedJWT jwt)
            throws JOSEException, BadJOSEException {

        jwt.verify(jwtManager.getVerifier());
        jwtManager.getJwtProcessor().process(jwt,null);
    }


    public String buildAccessToken(User user)
    {

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .expirationTime(Date.from(Instant.now().plus(this.jwtManager.getAccessTokenExpire())))
                .issueTime(Date.from(Instant.now()))
                .claim(JWTManager.CLAIM_ID, user.getId())
                .claim(JWTManager.CLAIM_ROLES, user.getRole())
                .build();

        JWSHeader header = new  JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                .keyID(jwtManager.getEcKey().getKeyID())
                .type(JWTManager.JWS_TYPE)
                .build();

        SignedJWT signedJWT = new SignedJWT(header,claimsSet);

        try {
            signedJWT.sign(jwtManager.getSigner());

        }catch(JOSEException e){
            e.printStackTrace();
        }

        String serialized = signedJWT.serialize();
        return serialized;

        //after successfully loggined we need to call this function for users
    }

    public void verifyAccessToken(String jws)
    {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jws);
            verifyJWT(signedJWT);

            if(Instant.now().isAfter(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant())) {
                //throw error
                throw new RuntimeException();
            }

        } catch (ParseException | BadJOSEException | JOSEException  e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public RefreshToken buildRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken()
                .setToken(this.generateUUID().toString())
                .setUserId(user.getId())
                .setTokenStatus(TokenStatus.ACTIVE)
                .setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()))
                .setMaxLifeTime(Instant.now().plus(this.jwtManager.getMaxRefreshTokenLifeTime()));

        return refreshToken;
    }

    public boolean hasExpired(RefreshToken refreshToken) {
        if(refreshToken.getTokenStatus() == TokenStatus.EXPIRED){
            return true;
        }
        return false;
    }

    public boolean needsRefresh(RefreshToken refreshToken) {
        if(refreshToken.getTokenStatus() == TokenStatus.REVOKED) {
            return true;
        }
        return false;
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken) {
        refreshToken.setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()));
    }

    private UUID generateUUID() {
        return UUID.randomUUID();
    }
}
