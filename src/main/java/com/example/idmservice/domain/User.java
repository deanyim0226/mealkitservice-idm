package com.example.idmservice.domain;


import com.example.idmservice.domain.type.Role;
import com.example.idmservice.domain.type.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
