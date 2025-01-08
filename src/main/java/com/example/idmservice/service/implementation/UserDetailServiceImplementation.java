package com.example.idmservice.service.implementation;


import com.example.idmservice.domain.User;
import com.example.idmservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImplementation implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User retrievedUser = userRepository.findByEmail(email).orElse(null);

        if(retrievedUser == null){
            throw  new UsernameNotFoundException(email);
        }



        return null;
    }
}
