package com.example.jwt.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {


    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP for Email Verification");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    public void sendEmail(String email, String passwordResetRequest, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(passwordResetRequest);
        message.setText("Password Rest Link: " + resetLink);
        mailSender.send(message);
    }
}
