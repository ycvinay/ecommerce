package com.example.jwt.controller;


import com.example.jwt.dto.JwtRequest;
import com.example.jwt.dto.JwtResponse;
import com.example.jwt.model.Role;
import com.example.jwt.model.User;
import com.example.jwt.service.JwtUtil;
import com.example.jwt.service.PasswordResetTokenService;
import com.example.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {


    @Autowired
    private PasswordResetTokenService resetTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest jwtRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtRequest.getEmail(), jwtRequest.getPassword()));

        User user = userService.getUserByEmail(jwtRequest.getEmail());
        String token = jwtUtil.generateToken(jwtRequest.getEmail(), user.getRole());
        return new JwtResponse(token, user.getRole());
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.sendOtp(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Boolean>> verifiedOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        boolean isVerified = userService.verifyOtp(email, otp);
        return ResponseEntity.ok(Map.of("verified", isVerified));
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        user.setRole(Role.USER);
        User user1 = userService.createUser(user);
        if (user1 == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email Already Exist");
        }
        return ResponseEntity.ok(user1);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            resetTokenService.createPasswordResetToken(email);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        try{
            resetTokenService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset Successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}

