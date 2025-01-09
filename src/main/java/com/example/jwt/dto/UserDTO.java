package com.example.jwt.dto;


import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String email;
    private String role;

    public UserDTO(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

}
