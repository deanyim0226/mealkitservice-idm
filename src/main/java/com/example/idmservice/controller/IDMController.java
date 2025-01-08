package com.example.idmservice.controller;


import com.example.idmservice.component.IDMJwtManager;
import com.example.idmservice.domain.RefreshToken;
import com.example.idmservice.domain.User;
import com.example.idmservice.model.request.AuthenticateRequest;
import com.example.idmservice.model.request.LoginRequest;
import com.example.idmservice.model.request.RefreshRequest;
import com.example.idmservice.model.response.LoginResponse;
import com.example.idmservice.model.response.RefreshResponse;
import com.example.idmservice.service.RefreshTokenService;
import com.example.idmservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin("http://localhost:4200/")
public class IDMController {


    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    IDMJwtManager jwtManager;

    @GetMapping("/getAllRegularUser")
    public ResponseEntity<?> getAllRegularUser(){

        List<User> regularUserList = userService.findAllRegularUser();

        return ResponseEntity.status(HttpStatus.OK).body(regularUserList);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUsers(){

        List<User> userList = userService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody User user){
        System.out.println("received user info from client " + user.getEmail());

        User existingUser = userService.findByEmail(user.getEmail());

        if(existingUser != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(user);
        }

        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        System.out.println("trying to login with email " + request.getEmail());
        User user = userService.findByEmail(request.getEmail());

        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(request);
        }

        if(!userService.checkUser(user, request.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(request);
        }

        String accessToken = jwtManager.buildAccessToken(user);
        RefreshToken refreshToken = jwtManager.buildRefreshToken(user);

        refreshTokenService.saveRefreshToken(refreshToken);

        String refreshTokenToString = refreshToken.getToken();

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenToString);

        System.out.println("access token is generated  " + accessToken);
        System.out.println("refresh token is generated " + refreshTokenToString);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /*
    refresh token flow 
    check whether refresh token is expired or not.
    check whether refresh token is revoked or not.
    check if currentTime is after refresh token expire time or max expire time
    if true, update the status of refresh token to be expired
    
    update refresh token expire time
    
    check if refresh token expire time is after refresh token max expire time  
    if true, update old refresh token status to be revoked and return new refresh token and access token
    
    update same refresh token expire time in DB
    return same refresh token and new access token 
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request)
    {
        String currentRefreshToken = request.getRefreshToken();

        if(currentRefreshToken.length() != 36) {
            throw new RuntimeException();
        }

        try {
            UUID.fromString(currentRefreshToken);
        }catch(IllegalArgumentException e) {
            throw new RuntimeException();
        }

        RefreshToken verifiedeRefreshToken = refreshTokenService.verifyRefreshToken(currentRefreshToken);

        if(jwtManager.hasExpired(verifiedeRefreshToken)) {
            throw new RuntimeException();
        }

        if(jwtManager.needsRefresh(verifiedeRefreshToken)) {
            throw new RuntimeException();
        }

        if(Instant.now().isAfter(verifiedeRefreshToken.getExpireTime()) || Instant.now().isAfter(verifiedeRefreshToken.getMaxLifeTime())) {
            //update refreshToken status to expired in DB
            refreshTokenService.expireRefreshToken(verifiedeRefreshToken);

            throw new RuntimeException();
        }

        jwtManager.updateRefreshTokenExpireTime(verifiedeRefreshToken);

        User user = refreshTokenService.getUserFromRefreshToken(verifiedeRefreshToken);

        if(verifiedeRefreshToken.getExpireTime().isAfter(verifiedeRefreshToken.getMaxLifeTime()))
        {
            refreshTokenService.revokeRefreshToken(verifiedeRefreshToken);

            String newAccessToken = jwtManager.buildAccessToken(user);
            RefreshToken newRefreshToken = jwtManager.buildRefreshToken(user);

            refreshTokenService.saveRefreshToken(newRefreshToken);

            RefreshResponse body = new RefreshResponse()
                    .setAccessToken(newAccessToken)
                    .setRefreshToken(newRefreshToken.getToken());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(body);
        }else{

            refreshTokenService.updateRefreshTokenExpireTime(verifiedeRefreshToken);

            String accessToken = jwtManager.buildAccessToken(user);

            RefreshResponse body = new RefreshResponse()
                    .setAccessToken(accessToken)
                    .setRefreshToken(verifiedeRefreshToken.getToken());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(body);
        }

    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenicate(@RequestBody AuthenticateRequest request)
    {
        System.out.println("autheticate is called");
        String accessToken = request.getAccessToken();

        if(accessToken.equals("") || accessToken.length() == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        jwtManager.verifyAccessToken(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        // You can return a ResponseEntity with a custom message and HTTP status code
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
    }
}
