package com.example.idmservice.service;

import com.example.idmservice.domain.User;

import java.util.List;

public interface UserService {
    public User saveUser(User newUser);

    public User findByEmail(String email);

    public boolean checkUser(User existingUser, String password);

    public List<User> findAllRegularUser();

    public List<User> findAll();
}
