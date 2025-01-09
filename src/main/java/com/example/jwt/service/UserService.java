package com.example.jwt.service;


import com.example.jwt.dto.UserDTO;
import com.example.jwt.model.Role;
import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private final Map<String, String> otpStore = new HashMap<>();

    public void sendOtp(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered.");
        }

        String otp = String.format("%6d", new Random().nextInt(999999));
        otpStore.put(email, otp);
        System.out.println(otp);
        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.removeIf(user -> user.getRole() == Role.ADMIN);
        return users;
    }

    public User getUserByEmail(String email) {
        Optional<User> users = userRepository.findByEmail(email);
        return users.orElse(null);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).get();
        UserDTO userDto = new UserDTO(user.getUsername(), user.getEmail(), user.getRole().name());
        return userDto;
    }

    public User editUser(Long userId,UserDTO updatedUser) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        user.setUsername(updatedUser.getUsername());
//        if (userRepository.existsByEmail(user.getEmail())) {
//            return new User();
//        }
        user.setEmail(updatedUser.getEmail());
        user.setRole(Role.valueOf(updatedUser.getRole()));

        return userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new RuntimeException("User with ID " + id + " does not exist");
        }
        userRepository.delete(existingUser);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current Password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String extractEmailFromToken(String token) {
        // Remove "Bearer " prefix from the token if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // Extract the email (subject) from the token
        return jwtUtil.extractUsername(token);
    }


}

