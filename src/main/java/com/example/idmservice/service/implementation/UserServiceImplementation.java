package com.example.idmservice.service.implementation;

import com.example.idmservice.domain.User;
import com.example.idmservice.domain.type.Role;
import com.example.idmservice.domain.type.UserStatus;
import com.example.idmservice.repository.UserRepository;
import com.example.idmservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Override
    public User saveUser(User newUser) {

        String encryptedPassword = encoder.encode(newUser.getPassword());
        newUser.setRole(Role.REGULAR);
        newUser.setPassword(encryptedPassword);
        newUser.setUserStatus(UserStatus.ACTIVE);
        return userRepository.save(newUser);
    }

    @Override
    public User findByEmail(String email) {

        User existingUser = userRepository.findByEmail(email).orElse(null);

        if(existingUser == null){
            return null;
        }
        return existingUser;
    }

    @Override
    public boolean checkUser(User existingUser, String password) {

        return encoder.matches(password, existingUser.getPassword());
    }

    @Override
    public List<User> findAllRegularUser() {
        List<User> userList = userRepository.findUsers();
        return userList;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
