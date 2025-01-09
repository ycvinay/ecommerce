package com.example.jwt.model;

import jakarta.persistence.*;
import lombok.*;
import com.example.jwt.model.Role;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Lob
    private byte[] profilePicture;

    @Transient
    private String imageBase64;

}
