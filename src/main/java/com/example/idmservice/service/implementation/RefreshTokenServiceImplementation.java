package com.example.idmservice.service.implementation;

import com.example.idmservice.domain.JWTManager;
import com.example.idmservice.domain.RefreshToken;
import com.example.idmservice.domain.User;
import com.example.idmservice.domain.type.TokenStatus;
import com.example.idmservice.repository.RefreshTokenRepository;
import com.example.idmservice.repository.UserRepository;
import com.example.idmservice.service.RefreshTokenService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class RefreshTokenServiceImplementation implements RefreshTokenService {
    private final Duration refreshTokenExpire = Duration.ofHours(12);
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void updateRefreshTokenExpireTime(RefreshToken token) {
        //update refresh token time

        try(Session session = sessionFactory.openSession()){

            session.beginTransaction();

            token.setExpireTime(Instant.now().plus(refreshTokenExpire));
            session.update(token);
            session.getTransaction().commit();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {
        //check token
        List<RefreshToken> tokenList = refreshTokenRepository.findAll();

        for(RefreshToken currentToken : tokenList){
            if(currentToken.getToken().equals(token)){
                return currentToken;
            }
        }

        return null;
    }

    @Override
    public void expireRefreshToken(RefreshToken token) {
        //update token to expired
        try(Session session = sessionFactory.openSession()){

            session.beginTransaction();
            token.setTokenStatus(TokenStatus.EXPIRED);
            session.update(token);

            session.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void revokeRefreshToken(RefreshToken token) {
        //update token to revoked

        try(Session session = sessionFactory.openSession()){

            session.beginTransaction();
            token.setTokenStatus(TokenStatus.REVOKED);
            session.update(token);

            session.getTransaction().commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public User getUserFromRefreshToken(RefreshToken refreshToken) {

        User retrievedUser = userRepository.findById(refreshToken.getId()).orElse(null);
        return retrievedUser;
    }


}
