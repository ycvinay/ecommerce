package com.example.jwt.controller;


import com.example.jwt.dto.UserDTO;
import com.example.jwt.model.User;
import com.example.jwt.service.JwtUtil;
import com.example.jwt.service.PasswordResetTokenService;
import com.example.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = userService.extractEmailFromToken(token);
        try {
            UserDTO userDetails = userService.getUserById(id);
            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDTO userDto, @RequestHeader("Authorization") String token) {
        String email = userService.extractEmailFromToken(token);

        User user = userService.editUser(id, userDto);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already existed");
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id){
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user: " + e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestHeader("Authorization") String token) {
        String email = userService.extractEmailFromToken(token);
        try {
            userService.changePassword(email, currentPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/upload-profile-picture")
    public ResponseEntity<User> uploadProfilePicture(@RequestHeader("Authorization") String token,
                                                     @RequestParam("file") MultipartFile file) throws IOException {
        String email = userService.extractEmailFromToken(token);
        User user = userService.getUserByEmail(email);

        user.setProfilePicture(file.getBytes());
        userService.save(user);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String token) {
        String email = userService.extractEmailFromToken(token);
        User user = userService.getUserByEmail(email);

        if (user.getProfilePicture() != null) {
            String base64 = Base64.getEncoder().encodeToString(user.getProfilePicture());
            user.setImageBase64(base64);
        }


        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("profilePicture", user.getProfilePicture());

        return ResponseEntity.ok(response);
    }


}

