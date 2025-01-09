package com.example.jwt.service;

import com.example.jwt.model.PasswordResetToken;
import com.example.jwt.model.User;
import com.example.jwt.repository.PasswordResetTokenRepository;
import com.example.jwt.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class PasswordResetTokenService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createPasswordResetToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("No user found with this email.");
        }

        User user = userOptional.get();

        tokenRepository.deleteByUser(user);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetToken.setUser(user);

        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:3000/reset-password/token=" + resetToken.getToken();

        emailService.sendEmail(user.getEmail(), "Password Reset Request", resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetTokenOptional = tokenRepository.findByToken(token);
        System.out.println(resetTokenOptional.get());
        if (!resetTokenOptional.isPresent()){
            throw new RuntimeException("Invalid or expired token.");
        }

        PasswordResetToken resetToken = resetTokenOptional.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has Expired.");
        }


        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

    }


}
